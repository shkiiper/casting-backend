package com.casting.platform.repository;

import com.casting.platform.entity.PromoCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PromoCode> findByCode(String code);
}
