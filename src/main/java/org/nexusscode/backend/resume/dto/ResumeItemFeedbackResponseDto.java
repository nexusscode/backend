package org.nexusscode.backend.resume.dto;

import lombok.Getter;

@Getter
public class ResumeItemFeedbackResponseDto {
    private Long id;
    private Long resumeItemId;
    private String feedbackText;

}
