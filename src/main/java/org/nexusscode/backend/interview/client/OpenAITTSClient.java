package org.nexusscode.backend.interview.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OpenAITTSClient {

    private final WebClient openAITTSWebClient;

    public OpenAITTSClient(@Qualifier("openAiTTSWebClient") WebClient openAITTSWebClient) {
        this.openAITTSWebClient = openAITTSWebClient;
    }

    public byte[] textToSpeech(String text, String voice, String instructions) {
        TTSRequest requestBody = new TTSRequest("gpt-4o-mini-tts", text, voice, instructions);

        return openAITTSWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    private record TTSRequest(String model, String input, String voice, String instructions) {}
}
