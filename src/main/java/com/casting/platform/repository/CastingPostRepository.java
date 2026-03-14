package com.casting.platform.repository;

import com.casting.platform.entity.CastingPost;
import com.casting.platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CastingPostRepository extends
        JpaRepository<CastingPost, Long>,
        JpaSpecificationExecutor<CastingPost> {

    Page<CastingPost> findByCustomerOrderByCreatedAtDesc(User customer,
                                                         Pageable pageable);
}
