package org.nexusscode.backend.resume.dto;

import lombok.Getter;

@Getter
public class ResumeFeedbackResponseDto {
    private Long id;
    private Long resumeItemId;
    private String feedbackText;

}
