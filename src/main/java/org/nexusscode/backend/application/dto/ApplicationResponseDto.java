package org.nexusscode.backend.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import org.nexusscode.backend.application.domain.JobApplication;

@Getter
public class ApplicationResponseDto {
    private Long id;
    private String saraminJobId;
    private String companyName;
    private String jobTitle;
    private String status;
    private LocalDateTime expirationDate;
    private String experienceLevel;
    private String jobCode;
    private String jobType;
    private String requiredEducationLevel;

    public ApplicationResponseDto(JobApplication application) {
        this.id=application.getId();
        this.saraminJobId= application.getSaraminJobId();
        this.companyName=application.getCompanyName();
        this.jobTitle= application.getJobTitle();
        this.status=application.getStatus().getStatus();
        this.expirationDate=application.getExpirationDate();
        this.experienceLevel=application.getExperienceLevel();
        this.jobCode= application.getJobCode();
        this.jobType= application.getJobType();
        this.requiredEducationLevel= application.getRequiredEducationLevel();
    }
}
