package org.nexusscode.backend.applicationReportMemo.repository;

import org.nexusscode.backend.applicationReportMemo.domain.ReportMemoInputSet;
import org.nexusscode.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportMemoInputSetRepository extends JpaRepository<ReportMemoInputSet, Long> {

    void deleteAllByUser(User user);
}
