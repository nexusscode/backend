package org.nexusscode.backend.application.dto;

import java.time.LocalDate;
import lombok.Getter;
import org.nexusscode.backend.application.domain.JobApplication;

@Getter
public class ApplicationResponseDto {
    private Long id;
    private String companyName;
    private String jobTitle;
    private String status;
    private LocalDate applicationDate;
    private String career;
    private String jobSource;

    public ApplicationResponseDto(JobApplication application) {
        this.id=application.getId();
        this.companyName=application.getCompanyName();
        this.jobTitle= application.getJobTitle();
        this.status=application.getStatus().getStatus();
        this.applicationDate=application.getApplicationDate();
        this.career=application.getCareer().getCareer();
        this.jobSource=application.getJobSource().getSource();
    }
}
