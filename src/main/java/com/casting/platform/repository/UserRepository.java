package com.casting.platform.repository;

import com.casting.platform.entity.User;
import com.casting.platform.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndRole(String email, UserRole role);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmailAndActiveTrue(String email);
    List<User> findByEmailOrderByIdAsc(String email);
    List<User> findByEmailOrderByIdDesc(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndRole(String email, UserRole role);
    boolean existsByPhone(String phone);

    default User findByIdAsUserDetails(Long id) {
        return findById(id).orElse(null);
    }
}
