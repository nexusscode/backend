package org.nexusscode.backend.user.dto;

import lombok.Getter;

@Getter
public class PasswordFindRequestDto {
    private String email;
    private String name;
    private String phoneNumber;

}
