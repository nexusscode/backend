package org.nexusscode.backend.user.repository;

import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.domain.UserStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatRepository extends JpaRepository<UserStat, Long> {

    void deleteAllByUser(User user);
}
