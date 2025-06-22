package org.nexusscode.backend.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.transcribe.TranscribeClient;

import java.time.Duration;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AIConfig {

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
                .baseUrl("https://api.openai.com/v1/audio/speech")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAITTSApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT, "audio/mpeg")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(30))
                ))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize( 1024 * 1024))
                        .build())
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

    @Bean
    public TranscribeClient transcribeClient() {
        return TranscribeClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

}