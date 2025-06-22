package org.nexusscode.backend.resume.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ResumeItemRequestDto {

    @NotBlank(message = "질문을 입력해주세요.")
    private String question;
    @NotNull(message = "답변을 입력해주세요.")
    @Size(min = 500, max = 1000, message = "답변은 500자 이상 1000자 이하로 입력해주세요.")
    private String answer;
}
