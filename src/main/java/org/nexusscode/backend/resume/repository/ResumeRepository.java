package org.nexusscode.backend.resume.repository;

import java.util.List;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    @EntityGraph(attributePaths = {"application", "resumeItems"})
    @Query("select r from Resume r where r.application.id = :applicationId")
    List<Resume> findAllByApplicationId(@Param("applicationId") Long applicationId);

    Resume findByApplication(JobApplication application);

    Resume findTopByUserOrderByModifiedAtDesc(User user);

    @Query("SELECT SUM(r.aiCount) FROM Resume r WHERE r.user = :user")
    Long sumAiCountByUser(@Param("user") User user);
}
