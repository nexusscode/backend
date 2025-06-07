package org.nexusscode.backend.application.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.resume.domain.Resume;

@Getter
public class ApplicationSimpleDto {
    private Long applicationId;
    private String companyName;
    private String jobTitle;
    private String experienceLevel;
    private LocalDateTime createdAt;
    private Long resumeId;
    private String ResumeFeedbackStatus;

    public ApplicationSimpleDto(JobApplication jobApplication, Resume resume) {
        this.applicationId= jobApplication.getId();
        this.companyName = jobApplication.getCompanyName();
        this.jobTitle = jobApplication.getJobTitle();
        this.experienceLevel = jobApplication.getExperienceLevel();
        this.createdAt = jobApplication.getCreatedAt();
        this.resumeId=resume.getId();
        this.ResumeFeedbackStatus=resume.getFeedbackStatus().getStatus();
    }
}
