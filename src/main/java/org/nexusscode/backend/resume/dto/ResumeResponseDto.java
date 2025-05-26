package org.nexusscode.backend.resume.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeItem;

@Getter
public class ResumeResponseDto {

    private Long id;
    private Long applicationId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long aiCount;

    public ResumeResponseDto(Resume resume) {
        this.id = resume.getId();
        this.applicationId = resume.getApplication().getId();
        this.createdAt=resume.getCreatedAt();
        this.modifiedAt=resume.getModifiedAt();
        this.aiCount= resume.getAiCount();
    }
}
