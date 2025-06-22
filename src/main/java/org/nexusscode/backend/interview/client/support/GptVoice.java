package org.nexusscode.backend.interview.client.support;

import lombok.Getter;

@Getter
public enum GptVoice {

    ALLOY("alloy"),
    ECHO("echo"),
    FABLE("fable"),
    ONYX("onyx"),
    NOVA("nova"),
    SHIMMER("shimmer");

    private final String voiceId;

    GptVoice(String voiceId) {
        this.voiceId = voiceId;
    }
}

