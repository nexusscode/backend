package org.nexusscode.backend.sms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.sms.dto.SmsVerifyRequestDto;
import org.nexusscode.backend.sms.service.SmsVerificationSerivce;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sms API", description = "인증 문자 관련 API")
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsVerificationController {
    private final SmsVerificationSerivce smsVerificationSerivce;

    @Operation(summary = "인증 문자 전송")
    @PostMapping("/send")
    public ResponseEntity<CommonResponse> sendSms(@RequestParam String phoneNumber){
        smsVerificationSerivce.sendSms(phoneNumber);
        CommonResponse response = new CommonResponse("문자 전송이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<CommonResponse> verifySms(@RequestBody SmsVerifyRequestDto smsVerifyRequestDto){
        smsVerificationSerivce.verifySms(smsVerifyRequestDto);
        CommonResponse response = new CommonResponse("문자 인증이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
