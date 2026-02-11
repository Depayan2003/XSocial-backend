package com.social.Repository;

import com.social.Model.BlockedUser;
import com.social.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockedUserRepository
        extends JpaRepository<BlockedUser, Long> {

    boolean existsByBlockerAndBlocked(User blocker, User blocked);

    Optional<BlockedUser> findByBlockerAndBlocked(
            User blocker,
            User blocked
    );
}
