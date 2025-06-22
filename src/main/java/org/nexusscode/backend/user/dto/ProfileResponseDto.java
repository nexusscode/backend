package org.nexusscode.backend.user.dto;

import lombok.Getter;
import org.nexusscode.backend.user.domain.User;

@Getter
public class ProfileResponseDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private String devType;
    private String experienceLevel;

    public ProfileResponseDto(User user) {
        this.id= user.getId();
        this.name=user.getName();
        this.phoneNumber=user.getPhoneNumber();
        this.devType=user.getDevType().getType();
        this.experienceLevel=user.getExperienceLevel().getLevel();
    }
}
