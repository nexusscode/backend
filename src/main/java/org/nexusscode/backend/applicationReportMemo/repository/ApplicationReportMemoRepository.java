package org.nexusscode.backend.applicationReportMemo.repository;

import org.nexusscode.backend.applicationReportMemo.domain.ApplicationReportMemo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.nexusscode.backend.user.domain.User;

import java.util.List;

@Repository
public interface ApplicationReportMemoRepository extends JpaRepository<ApplicationReportMemo, Long> {
    List<ApplicationReportMemo> findByUserId(Long userId);
    List<ApplicationReportMemo> findAllByUser(User user);
}
