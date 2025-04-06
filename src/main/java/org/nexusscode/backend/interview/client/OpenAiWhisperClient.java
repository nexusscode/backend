package org.nexusscode.backend.interview.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OpenAiWhisperClient {

    private final WebClient webClient;

    public OpenAiWhisperClient(@Qualifier("openaiTranscriptionWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public String transcribe(String filePath) {
        FileSystemResource audioFile = new FileSystemResource(filePath);

        return webClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", audioFile)
                        .with("model", "gpt-4o-transcribe"))
                .retrieve()
                .bodyToMono(TranscriptionResponse.class)
                .map(TranscriptionResponse::text)
                .block();
    }

    private record TranscriptionResponse(String text) {}
}
