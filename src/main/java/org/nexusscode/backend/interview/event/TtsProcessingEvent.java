package org.nexusscode.backend.interview.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.interview.client.support.GptVoice;
import org.nexusscode.backend.interview.domain.InterviewQuestion;

@Getter
@AllArgsConstructor
public class TtsProcessingEvent {
    private final InterviewQuestion question;
    private final GptVoice voice;
}
