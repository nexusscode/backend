package org.nexusscode.backend.user.repository;

import org.nexusscode.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByNameAndPhoneNumber(String name, String phoneNumber);

    User findByEmailAndNameAndPhoneNumber(String email, String name, String phoneNumber);
}
