package org.nexusscode.backend.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ApplicationRequestDto {
    @NotBlank(message = "사람인 공고의 고유 Id를 입력해주세요")
    private String saraminJobId;
}
