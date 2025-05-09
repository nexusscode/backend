package org.nexusscode.backend.interview.client;

import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.CustomException.EmptyGPTResponseException;
import org.nexusscode.backend.interview.client.support.GptVoice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@Log4j2
public class OpenAITTSClient {

    private final WebClient openAITTSWebClient;

    public OpenAITTSClient(@Qualifier("openAiTTSWebClient") WebClient openAITTSWebClient) {
        this.openAITTSWebClient = openAITTSWebClient;
    }

    public Mono<byte[]> textToSpeechAsync(String text, GptVoice voice) {
        TTSRequest requestBody = new TTSRequest("tts-1", text, voice.getVoiceId(), "mp3");

        return openAITTSWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(byte[].class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .onErrorMap(e -> {
                    log.error(e.getMessage(), e);
                    throw new CustomException(ErrorCode.TTS_FAILED);
                });
    }

    public byte[] textToSpeechSync(String text, GptVoice voice) {
        return textToSpeechAsync(text, voice).block();
    }

    private record TTSRequest(String model, String input, String voice, String response_format) {}
}
