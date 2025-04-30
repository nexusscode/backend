package org.nexusscode.backend.resume.dto;

import java.util.List;
import lombok.Getter;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeItem;

@Getter
public class ResumeResponseDto {
    private Long id;
    private Long applicationId;
    private String title;
    private List<ResumeItem> resumeItems;

    public ResumeResponseDto(Resume resume) {
        this.id= resume.getId();
        this.applicationId=resume.getApplication().getId();
        this.title= resume.getTitle();
    }
}
