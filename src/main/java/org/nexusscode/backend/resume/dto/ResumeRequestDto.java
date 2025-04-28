package org.nexusscode.backend.resume.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResumeRequestDto {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
}
