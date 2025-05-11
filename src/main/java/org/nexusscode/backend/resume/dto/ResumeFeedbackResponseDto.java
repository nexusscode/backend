package org.nexusscode.backend.resume.dto;

import lombok.Getter;
import org.nexusscode.backend.resume.domain.ResumeFeedback;

@Getter
public class ResumeFeedbackResponseDto {
    private Long id;
    private Long resumeItemId;
    private String feedbackText;

    public ResumeFeedbackResponseDto(ResumeFeedback resumeFeedback) {
        this.id=resumeFeedback.getId();
        this.resumeItemId=resumeFeedback.getResumeItem().getId();
        this.feedbackText= resumeFeedback.getFeedbackText();
    }
}
