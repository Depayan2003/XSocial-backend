package com.social.Repository;

import com.social.Model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByEmail(String email);

    void deleteByEmail(String email);
}
