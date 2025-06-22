package org.nexusscode.backend.user.dto;

import lombok.Getter;

@Getter
public class ProfileUpdateRequestDto {
    private String name;
    private String phoneNumber;
    private String devType;
    private String experienceLevel;

}
