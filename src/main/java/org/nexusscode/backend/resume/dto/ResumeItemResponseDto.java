package org.nexusscode.backend.resume.dto;

import lombok.Getter;
import org.nexusscode.backend.resume.domain.ResumeItem;

@Getter
public class ResumeItemResponseDto {

    private Long id;
    private Long resumeId;
    private String question;
    private String answer;

    public ResumeItemResponseDto(ResumeItem resumeItem) {
        this.id = resumeItem.getId();
        this.resumeId = resumeItem.getResume().getId();
        this.question = resumeItem.getQuestion();
        this.answer = resumeItem.getAnswer();
    }
}
