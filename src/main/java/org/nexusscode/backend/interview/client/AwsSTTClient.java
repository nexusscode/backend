package org.nexusscode.backend.interview.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Log4j2
public class AwsSTTClient {
    private final TranscribeClient transcribeClient;
    private final ObjectMapper objectMapper;

    @Value("${}")
    private static String BUCKET_NAME;

    @Value("${}")
    private static String UPLOAD_PATH;

    public String convertAudioText(String audioUrl) {
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

        while (true) {
            TranscriptionJob job = transcribeClient.getTranscriptionJob(
                    GetTranscriptionJobRequest.builder()
                            .transcriptionJobName(jobName)
                            .build()
            ).transcriptionJob();

            TranscriptionJobStatus status = job.transcriptionJobStatus();
            if (status == TranscriptionJobStatus.COMPLETED) return job;
            if (status == TranscriptionJobStatus.FAILED) {
                throw new RuntimeException("STT 실패: " + job.failureReason());
            }

            if (Duration.between(start, Instant.now()).toMinutes() > 2) {
                throw new RuntimeException("STT 타임아웃");
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("STT 대기 중 인터럽트", e);
            }
        }
    }

    private String fetchTranscriptText(String transcriptUrl) {
        try (InputStream is = new URL(transcriptUrl).openStream()) {
            JsonNode root = objectMapper.readTree(is);
            JsonNode transcripts = root.path("results").path("transcripts");

            if (transcripts.isArray() && transcripts.size() > 0) {
                return transcripts.get(0).path("transcript").asText();
            } else {
                throw new RuntimeException("transcript 필드가 비어있습니다");
            }
        } catch (Exception e) {
            throw new RuntimeException("transcript JSON 파싱 실패", e);
        }
    }

    private String detectMediaFormat(String url) {
        if (url.endsWith(".mp3")) return "mp3";
        if (url.endsWith(".mp4")) return "mp4";
        if (url.endsWith(".wav")) return "wav";
        if (url.endsWith(".flac")) return "flac";
        throw new IllegalArgumentException("지원하지 않는 오디오 형식입니다: " + url);
    }

    public static String generatePresignedUrl(String fileName, Duration expiresIn) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(UPLOAD_PATH + "/" + fileName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(expiresIn)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();
        }
    }
}
