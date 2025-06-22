package org.nexusscode.backend.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class UserModifyDTO {
    private Long id;
    private String email;
    private String password;
    private String name;
}
