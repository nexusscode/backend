package org.nexusscode.backend.application.dto;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ApplicationUpdateRequestDto {
    private String companyName;
    private String jobTitle;
    private LocalDate applicationDate;
    private String career;
    private String jobSource;
    private String status;
}
