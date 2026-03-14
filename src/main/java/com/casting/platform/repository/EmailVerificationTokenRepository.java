package com.casting.platform.repository;

import com.casting.platform.entity.EmailVerificationToken;
import com.casting.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken>
    findTopByUserOrderByExpiresAtDesc(User user);

    void deleteByUserId(Long userId);
}