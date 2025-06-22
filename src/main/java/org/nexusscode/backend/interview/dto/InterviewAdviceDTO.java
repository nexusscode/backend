package org.nexusscode.backend.interview.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InterviewAdviceDTO {
    private Long questionId;
    private String questionText;
    private Long answerId;
    private String transcript;
    private Long feedbackId;
    private String feedbackText;
}
