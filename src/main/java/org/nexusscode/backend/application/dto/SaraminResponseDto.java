package org.nexusscode.backend.application.dto;

import lombok.Getter;

@Getter
public class SaraminResponseDto {
    String id;
    private String companyName;
    private String title;

    public SaraminResponseDto(String id,String companyName, String title) {
        this.id=id;
        this.companyName=companyName;
        this.title=title;
    }
}
