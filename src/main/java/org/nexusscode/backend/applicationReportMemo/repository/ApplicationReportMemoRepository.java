package org.nexusscode.backend.applicationReportMemo.repository;

import org.nexusscode.backend.applicationReportMemo.domain.ApplicationReportMemo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.nexusscode.backend.user.domain.User;

import java.util.List;

@Repository
public interface ApplicationReportMemoRepository extends JpaRepository<ApplicationReportMemo, Long> {
    List<ApplicationReportMemo> findByUserId(Long userId);
    List<ApplicationReportMemo> findAllByUser(User user);

    @Query("""
    SELECT m FROM ApplicationReportMemo m
    WHERE m.user = :user AND m.isSaved = true AND (
      LOWER(m.companyName) LIKE LOWER(CONCAT('%', :searchWord, '%')) OR
      LOWER(m.position) LIKE LOWER(CONCAT('%', :searchWord, '%'))
    )
    """)
    Page<ApplicationReportMemo> searchSavedByUserAndCompanyOrPosition(
            @Param("user") User user,
            @Param("searchWord") String searchWord,
            Pageable pageable);

    Page<ApplicationReportMemo> findAllByUserAndIsSaved(User user, boolean isSaved, Pageable pageable);
}
