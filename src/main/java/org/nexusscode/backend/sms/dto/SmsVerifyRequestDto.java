package org.nexusscode.backend.sms.dto;

import lombok.Getter;

@Getter
public class SmsVerifyRequestDto {

    private String phoneNumber;
    private String code;
}
