package org.nexusscode.backend.interview.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Log4j2
public class AwsClient {
    private final TranscribeClient transcribeClient;
    private final ObjectMapper objectMapper;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.user-upload-path}")
    private String userVoiceUploadPath;

    @Value("${aws.s3.ai-upload-path}")
    private String aiVoiceUploadPath;

    @Value("${aws.s3.ai-video-path}")
    private String aiVideoUploadPath;

    public Map<String, Object> convertAudioText(String audioUrl) {
        String jobName = "job-" + System.currentTimeMillis();
        log.info("Transcribe 시작: {}", jobName);

        StartTranscriptionJobRequest request = StartTranscriptionJobRequest.builder()
                .transcriptionJobName(jobName)
                .languageCode(LanguageCode.KO_KR)
                .mediaFormat(detectMediaFormat(audioUrl))
                .media(Media.builder().mediaFileUri(audioUrl).build())
                .build();

        transcribeClient.startTranscriptionJob(request);

        TranscriptionJob job = waitForJobCompletion(jobName);
        String transcriptUrl = job.transcript().transcriptFileUri();

        return fetchTranscriptText(transcriptUrl);
    }

    private TranscriptionJob waitForJobCompletion(String jobName) {
        Instant start = Instant.now();
        int retryCount = 0;
        int maxRetries = 40;

        while (retryCount++ < maxRetries) {
            TranscriptionJob job = transcribeClient.getTranscriptionJob(
                    GetTranscriptionJobRequest.builder().transcriptionJobName(jobName).build()
            ).transcriptionJob();

            switch (job.transcriptionJobStatus()) {
                case COMPLETED: return job;
                case FAILED:
                    log.error("STT 실패: {}", job.failureReason());
                    throw new CustomException(ErrorCode.STT_FAILED);
            }

            try {
                Thread.sleep(Math.min(3000L * retryCount, 5000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("STT 대기 중 인터럽트", e);
            }
        }

        log.error("STT 타임아웃: jobName = {}", jobName);
        throw new RuntimeException("STT 타임아웃");
    }


    private Map<String, Object> fetchTranscriptText(String transcriptUrl) {
        try (InputStream is = new URL(transcriptUrl).openStream()) {
            JsonNode root = objectMapper.readTree(is);

            JsonNode transcripts = root.path("results").path("transcripts");
            if (!transcripts.isArray() || transcripts.isEmpty()) {
                log.error("transcript 필드가 비어있습니다");
                throw new RuntimeException("transcript 필드가 비어있습니다");
            }
            String transcript = transcripts.get(0).path("transcript").asText();

            JsonNode audioSegments = root.path("results").path("audio_segments");
            if (!audioSegments.isArray() || audioSegments.isEmpty()) {
                log.error("audio_segments 필드가 비어있습니다");
                throw new RuntimeException("audio_segments 필드가 비어있습니다");
            }

            // 전체 구간의 start_time, end_time을 이용해서 duration 계산
            double start = Double.MAX_VALUE;
            double end = Double.MIN_VALUE;

            for (JsonNode segment : audioSegments) {
                double segmentStart = segment.path("start_time").asDouble(-1);
                double segmentEnd = segment.path("end_time").asDouble(-1);

                if (segmentStart >= 0) {
                    start = Math.min(start, segmentStart);
                }
                if (segmentEnd >= 0) {
                    end = Math.max(end, segmentEnd);
                }
            }

            if (start == Double.MAX_VALUE || end == Double.MIN_VALUE) {
                log.error("audio_segments의 start_time 또는 end_time이 올바르지 않습니다");
                throw new RuntimeException("audio_segments의 start_time 또는 end_time이 올바르지 않습니다");
            }

            int duration = (int) Math.round(end - start);

            Map<String, Object> result = new HashMap<>();
            result.put("transcript", transcript);
            result.put("duration", duration);

            return result;
        } catch (Exception e) {
            log.error("transcript JSON 파싱 실패");
            throw new RuntimeException("transcript JSON 파싱 실패", e);
        }
    }


    private String detectMediaFormat(String url) {
        if (url.endsWith(".mp3")) return "mp3";
        if (url.endsWith(".mp4")) return "mp4";
        if (url.endsWith(".webm")) return "webm";
        if (url.endsWith(".m4a")) return "m4a";
        if (url.endsWith(".wav")) return "wav";
        if (url.endsWith(".flac")) return "flac";
        log.error("지원하지 않는 오디오 형식입니다: " + url);
        throw new IllegalArgumentException("지원하지 않는 오디오 형식입니다: " + url);
    }

    private String getContentTypeByFormat(String format) {
        return switch (format) {
            case "mp3" -> "audio/mpeg";
            case "mp4" -> "video/mp4";      // 또는 audio/mp4 depending on actual media
            case "webm" -> "video/webm";
            case "m4a" -> "audio/mp4";
            case "wav" -> "audio/wav";
            case "flac" -> "audio/flac";
            default -> throw new IllegalArgumentException("지원하지 않는 오디오 형식입니다: " + format);
        };
    }

    public String generateUserVoicePresignedUrl(String fileName, Duration expiresIn) {
        return generatePresignedUrlGet(userVoiceUploadPath, fileName, expiresIn);
    }

    public String generateAIVoicePresignedUrl(String fileName, Duration expiresIn) {
        return generatePresignedUrlGet(aiVoiceUploadPath, fileName, expiresIn);
    }

    public String generateAIVideoPresignedUrl(String fileName, Duration expiresIn) {
        return generatePresignedUrlGet(aiVideoUploadPath, fileName, expiresIn);
    }

    public String generateUserVoicePutPresignURL(String fileName, Duration expiresIn) {
        return generatePresignedPutUrl(userVoiceUploadPath, fileName, expiresIn);
    }


    private String generatePresignedUrlGet(String prefixPath, String fileName, Duration expiresIn) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .build()) {

            String key = prefixPath + "/" + fileName;

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(expiresIn)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();

        } catch (Exception e) {
            log.error("Presigned URL 생성 실패 - path: {}/{}", prefixPath, fileName, e);
            throw new RuntimeException("Presigned URL 생성 실패");
        }
    }

    private String generatePresignedPutUrl(String prefixPath, String fileName, Duration expiresIn) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .build()) {

            String key = prefixPath + "/" + fileName;
            String format = detectMediaFormat(fileName);
            String contentType = getContentTypeByFormat(format);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .putObjectRequest(putObjectRequest)
                    .signatureDuration(expiresIn)
                    .build();

            return presigner.presignPutObject(presignRequest).url().toString();

        } catch (Exception e) {
            log.error("Presigned PUT URL 생성 실패 - path: {}/{}", prefixPath, fileName, e);
            throw new RuntimeException("Presigned URL 생성 실패");
        }
    }


    @Async
    public CompletableFuture<String> uploadTtsAudio(byte[] audioData, String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String key = aiVoiceUploadPath + "/" + fileName;

                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType("audio/mpeg")
                        .build();

                s3Client.putObject(putRequest, RequestBody.fromBytes(audioData));

                return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
            } catch (Exception e) {
                throw new CustomException(ErrorCode.S3_UPLOAD_FAILED);
            }
        });
    }




}
