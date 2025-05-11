package org.nexusscode.backend.survey.repository;

import org.nexusscode.backend.survey.domain.SurveyResult;
import org.nexusscode.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyResultRepository extends JpaRepository<SurveyResult,Long> {

    SurveyResult findByUser(User user);
}
