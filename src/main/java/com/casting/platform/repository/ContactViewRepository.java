package com.casting.platform.repository;

import com.casting.platform.entity.ContactView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactViewRepository extends JpaRepository<ContactView, Long> {

    Optional<ContactView> findByCustomerIdAndProfileId(Long customerId, Long profileId);

    Page<ContactView> findByCustomerIdOrderByViewedAtDesc(Long customerId, Pageable pageable);

}
