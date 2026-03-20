package com.casting.platform.repository;

import com.casting.platform.entity.ContactView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface ContactViewRepository extends JpaRepository<ContactView, Long> {

    Optional<ContactView> findByCustomerIdAndProfileId(Long customerId, Long profileId);

    Page<ContactView> findByCustomerIdOrderByViewedAtDesc(Long customerId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    void deleteByCustomerId(Long customerId);

    @Modifying(clearAutomatically = true)
    void deleteByProfileId(Long profileId);

}
