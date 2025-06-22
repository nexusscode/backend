package org.nexusscode.backend.resume.dto;

import lombok.Getter;

@Getter
public class SavedResumeResponseDto {
    private Long resumeId;
    private String companyName;

    public SavedResumeResponseDto(Long id, String companyName) {
        this.resumeId=id;
        this.companyName=companyName;
    }
}
