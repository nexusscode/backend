package org.nexusscode.backend.user.dto;

import lombok.Getter;

@Getter
public class UserRequestDto {
    private String email;
    private String password;
    private String name;

}
