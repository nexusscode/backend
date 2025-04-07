package org.nexusscode.backend.interview.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InterviewAnswerRequest {
    private Long questionId;
    private String audioUrl;
}
