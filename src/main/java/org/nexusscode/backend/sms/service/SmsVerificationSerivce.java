package org.nexusscode.backend.sms.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.sms.SmsUtil;
import org.nexusscode.backend.sms.domain.SmsVerification;
import org.nexusscode.backend.sms.dto.SmsVerifyRequestDto;
import org.nexusscode.backend.sms.repository.SmsVerificationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsVerificationSerivce {
    private final SmsVerificationRepository smsVerificationRepository;
    private final SmsUtil smsUtil;

    public void sendSms(String phoneNumber) {
        // 인증 코드 6자리
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        smsUtil.sendOne(phoneNumber,code);
        // 만료 시간 3분
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(3);

        SmsVerification smsVerification = SmsVerification.builder()
            .phoneNumber(phoneNumber)
            .verificationCode(code)
            .expirationTime(expirationTime)
            .build();

        smsVerificationRepository.save(smsVerification);
    }

    public void verifySms(SmsVerifyRequestDto smsVerifyRequestDto) {
        SmsVerification smsVerification = smsVerificationRepository.findByPhoneNumberAndVerificationCode(smsVerifyRequestDto.getPhoneNumber(),smsVerifyRequestDto.getCode());
        if(smsVerification==null){
            throw new CustomException(ErrorCode.NOT_FOUND_SMS_VERIFICATION);
        }
        if(smsVerification.getExpirationTime().isBefore(LocalDateTime.now())){
            smsVerificationRepository.delete(smsVerification);
            throw new CustomException(ErrorCode.EXPIRED_SMS_VERIFICATION);
        }

        smsVerificationRepository.delete(smsVerification);
    }
}
