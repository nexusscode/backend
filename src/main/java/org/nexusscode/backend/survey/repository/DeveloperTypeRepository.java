package org.nexusscode.backend.survey.repository;

import java.util.Optional;
import org.nexusscode.backend.survey.domain.DeveloperType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperTypeRepository extends JpaRepository<DeveloperType,Long> {

    Optional<DeveloperType> findByName(String primaryType);
}
