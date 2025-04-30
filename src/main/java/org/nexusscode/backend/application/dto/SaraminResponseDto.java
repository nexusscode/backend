package org.nexusscode.backend.application.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class SaraminResponseDto {
    private String id;
    private String companyName;
    private String title;
    private LocalDateTime expirationDate;
    private String experienceLevel;

    public SaraminResponseDto(String id,String companyName, String title,LocalDateTime expirationDate,String experienceLevel) {
        this.id=id;
        this.companyName=companyName;
        this.title=title;
        this.expirationDate=expirationDate;
        this.experienceLevel=experienceLevel;
    }
}
