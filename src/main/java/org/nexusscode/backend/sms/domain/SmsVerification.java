package org.nexusscode.backend.sms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sms_verifications")
@Getter
@NoArgsConstructor
public class SmsVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    @Builder
    public SmsVerification(String phoneNumber,String verificationCode, LocalDateTime expirationTime) {
        this.phoneNumber=phoneNumber;
        this.verificationCode = verificationCode;
        this.expirationTime = expirationTime;
    }
}
