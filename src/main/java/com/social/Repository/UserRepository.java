package com.social.Repository;

import com.social.Model.User;
import com.social.Model.Enums.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name,
            String email
    );
    
}
