package org.nexusscode.backend.interview.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class AdviceGenerationEvent {
    private final Long questionId;
    private final String audioUrl;
}
