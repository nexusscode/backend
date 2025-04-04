package org.nexusscode.backend.memo.repository;

import org.nexusscode.backend.memo.domain.ApplicationMemo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationMemoRepository extends JpaRepository<ApplicationMemo, Long> {
}
