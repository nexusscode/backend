package org.nexusscode.backend.user.repository;

import org.nexusscode.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
