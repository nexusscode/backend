package org.nexusscode.backend.sms.controller;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.sms.service.SmsVerificationSerivce;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsVerificationController {
    private final SmsVerificationSerivce smsVerificationSerivce;

    @PostMapping("/send")
    public ResponseEntity<CommonResponse> sendSms(@RequestParam String phoneNumber){
        smsVerificationSerivce.sendSms(phoneNumber);
        CommonResponse response = new CommonResponse("문자 전송이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
