package org.nexusscode.backend.application.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.resume.domain.ResumeFeedbackStatus;

@Getter
public class ApplicationSimpleDto {
    private Long applicationId;
    private String companyName;
    private String jobTitle;
    private String experienceLevel;
    private LocalDateTime createdAt;
    private String ResumeFeedbackStatus;

    public ApplicationSimpleDto(JobApplication jobApplication, ResumeFeedbackStatus resumeFeedbackStatus) {
        this.applicationId= jobApplication.getId();
        this.companyName = jobApplication.getCompanyName();
        this.jobTitle = jobApplication.getJobTitle();
        this.experienceLevel = jobApplication.getExperienceLevel();
        this.createdAt = jobApplication.getCreatedAt();
        this.ResumeFeedbackStatus=resumeFeedbackStatus.getStatus();
    }
}
