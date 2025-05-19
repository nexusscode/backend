package org.nexusscode.backend.sms.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.sms.SmsUtil;
import org.nexusscode.backend.sms.domain.SmsVerification;
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
}
