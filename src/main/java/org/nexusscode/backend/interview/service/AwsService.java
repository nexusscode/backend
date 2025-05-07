package org.nexusscode.backend.interview.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.interview.client.AwsClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsService {

    private final AwsClient awsClient;

    public String uploadTtsAudio(byte[] audioData) {
        String uuid = UUID.randomUUID().toString();

        String fileName = uuid + ".mp3";

        awsClient.uploadTtsAudio(audioData, fileName);

        return fileName;
    }

    public Map<String, Object> convertAudioText(String audioUrl) {
        return awsClient.convertAudioText(audioUrl);
    }
}
