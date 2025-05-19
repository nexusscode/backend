package org.nexusscode.backend.sms.repository;

import org.nexusscode.backend.sms.domain.SmsVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsVerificationRepository extends JpaRepository<SmsVerification,Long> {

}
