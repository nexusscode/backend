package org.nexusscode.backend.application.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ApplicationRequestDto {
    private String saraminJobId;
    /*@NotBlank(message = "회사 이름을 입력해주세요.")
    private String companyName;
    @NotBlank(message = "공고 제목을 입력해주세요.")
    private String jobTitle;
    @NotBlank(message = "공고 마감일자를 입력해주세요.")
    private LocalDate applicationDate;
    @NotBlank(message = "경력 수준을 입력해주세요.")
    private String career;
    @NotBlank(message = "공고 출처를 입력해주세요.")
    private String jobSource;*/
}
