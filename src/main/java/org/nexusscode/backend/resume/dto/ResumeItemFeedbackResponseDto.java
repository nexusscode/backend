package org.nexusscode.backend.resume.dto;

import lombok.Getter;
import org.nexusscode.backend.resume.domain.ResumeItemFeedback;

@Getter
public class ResumeItemFeedbackResponseDto {
    private Long feedbackId;
    private Long resumeItemId;
    private String feedbackText;

    public ResumeItemFeedbackResponseDto(ResumeItemFeedback resumeItemFeedback) {
        this.feedbackId= resumeItemFeedback.getId();
        this.resumeItemId= resumeItemFeedback.getResumeItem().getId();
        this.feedbackText= resumeItemFeedback.getFeedbackText();
    }
}
