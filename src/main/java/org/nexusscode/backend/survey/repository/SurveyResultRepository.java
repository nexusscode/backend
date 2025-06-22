package org.nexusscode.backend.survey.repository;

import java.util.Optional;
import org.nexusscode.backend.survey.domain.SurveyResult;
import org.nexusscode.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyResultRepository extends JpaRepository<SurveyResult,Long> {

    Optional<SurveyResult> findByUser(User user);
}
