package org.nexusscode.backend.application.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import org.nexusscode.backend.application.domain.JobApplication;

@Getter
public class ApplicationSimpleDto {
    private Long applicationId;
    private String companyName;
    private String jobTitle;
    private String experienceLevel;
    private LocalDateTime createdAt;

    public ApplicationSimpleDto(JobApplication jobApplication) {
        this.applicationId= jobApplication.getId();
        this.companyName = jobApplication.getCompanyName();
        this.jobTitle = jobApplication.getJobTitle();
        this.experienceLevel = jobApplication.getExperienceLevel();
        this.createdAt = jobApplication.getCreatedAt();
    }
}
