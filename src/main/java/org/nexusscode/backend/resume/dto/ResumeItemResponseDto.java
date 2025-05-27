package org.nexusscode.backend.resume.dto;

import lombok.Getter;
import org.nexusscode.backend.resume.domain.ResumeItem;

@Getter
public class ResumeItemResponseDto {

    private Long resumeItemId;
    private Long resumeId;
    private String question;
    private String answer;
    private Long aiCount;

    public ResumeItemResponseDto(ResumeItem resumeItem) {
        this.resumeItemId = resumeItem.getId();
        this.resumeId = resumeItem.getResume().getId();
        this.question = resumeItem.getQuestion();
        this.answer = resumeItem.getAnswer();
        this.aiCount=resumeItem.getAiCount();
    }
}
