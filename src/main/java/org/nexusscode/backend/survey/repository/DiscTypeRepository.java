package org.nexusscode.backend.survey.repository;

import java.util.Optional;
import org.nexusscode.backend.survey.domain.DiscEnum;
import org.nexusscode.backend.survey.domain.DiscType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscTypeRepository extends JpaRepository<DiscType,Long> {

    Optional<DiscType> findByDisc(DiscEnum disc);
}
