package org.nexusscode.backend.resume.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResumeItemRequestDto {
    @NotBlank(message = "질문을 입력해주세요.")
    private String question;
    @NotBlank(message = "답변을 입력해주세요.")
    private String answer;
    @NotBlank(message = "순서를 입력해주세요.")
    private int seq;
    @NotBlank(message = "제한 글자수를 입력해주세요.")
    private int wordLimit;
}
