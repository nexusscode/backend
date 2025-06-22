package org.nexusscode.backend.user.repository;

import org.nexusscode.backend.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  
    @EntityGraph(attributePaths = {"userRoleList"})
    @Query("select s from User s where s.email = :email")
    Optional<User> getWithRoles(@Param("email")String email);

    Optional<User> findByEmail(String email);

    User findByNameAndPhoneNumber(String name, String phoneNumber);

    User findByEmailAndNameAndPhoneNumber(String email, String name, String phoneNumber);
}
