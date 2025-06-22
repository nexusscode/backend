package org.nexusscode.backend.resume.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class RecentResumeResponseDto {
    private Long resumeId;
    private String applicationTitle;
    private LocalDateTime modifiedAt;

    public RecentResumeResponseDto(Long id, String jobTitle, LocalDateTime modifiedAt) {
        this.resumeId=id;
        this.applicationTitle=jobTitle;
        this.modifiedAt=modifiedAt;
    }
}
