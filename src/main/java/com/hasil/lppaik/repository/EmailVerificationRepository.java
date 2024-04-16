package com.hasil.lppaik.repository;

import com.hasil.lppaik.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
  Optional<EmailVerification> findByUserUsername(String username);
}
