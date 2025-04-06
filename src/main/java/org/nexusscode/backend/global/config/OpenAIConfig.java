package org.nexusscode.backend.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class OpenAIConfig {

    @Value("${spring.ai.openai.api-key}")
    private String openAITTSApiKey;

    @Value("${org.nexusscode.openAITTS.tts-endpoint}")
    private String ttsEndpoint;

    @Value("${spring.ai.openai.api-key}")
    private String openAIWhisperApiKey;

    @Value("${org.nexusscode.openAIWhisper.whisper-endpoint}")
    private String whisperEndpoint;

    @Bean
    public ChatClient chatClient(ChatModel model) {
        return ChatClient.create(model);
    }

    @Bean
    @Qualifier("openAiWebClient")
    public WebClient openAiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.openai.com")
                .build();
    }

    @Bean
    @Qualifier("openAiTTSWebClient")
    public WebClient openAiTTSWebClient() {
        return WebClient.builder()
                .baseUrl(ttsEndpoint)
                .defaultHeader("Authorization", "Bearer " + openAITTSApiKey)
                .defaultHeader("Content-Type", "application/json")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(30))
                ))
                .build();
    }

    @Bean
    @Qualifier("openaiTranscriptionWebClient")
    public WebClient openaiTranscriptionWebClient() {
        return WebClient.builder()
                .baseUrl(whisperEndpoint)
                .defaultHeader("Authorization", "Bearer " + openAIWhisperApiKey)
                .build();
    }
}