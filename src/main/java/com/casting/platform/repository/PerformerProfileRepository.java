package com.casting.platform.repository;

import com.casting.platform.entity.PerformerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PerformerProfileRepository
        extends JpaRepository<PerformerProfile, Long> {

    /* ================= PROFILE ================= */

    @Query(value = """
            SELECT *
            FROM performer_profiles
            WHERE owner_id = :ownerId
            LIMIT 1
            """,
            nativeQuery = true)
    Optional<PerformerProfile> findByOwnerId(Long ownerId);


    /* ================= ACTORS ================= */

    @Query(value = """
            SELECT *
            FROM performer_profiles p
            WHERE p.type = :type
              AND p.published = true
              AND (
                    (p.premium_until IS NOT NULL AND p.premium_until > CURRENT_TIMESTAMP)
                    OR (
                        (:city IS NULL OR p.city ILIKE CONCAT('%', :city, '%'))
                        AND (:minAge IS NULL OR p.age >= :minAge)
                        AND (:maxAge IS NULL OR p.age <= :maxAge)
                        AND (:gender IS NULL OR p.gender = :gender)
                        AND (:ethnicity IS NULL OR p.ethnicity = :ethnicity)
                        AND (:minRate IS NULL OR p.min_rate >= :minRate)
                        AND (:maxRate IS NULL OR p.min_rate <= :maxRate)
                        AND (:rateUnit IS NULL OR p.rate_unit = :rateUnit)
                    )
              )
            ORDER BY
              CASE
                WHEN p.premium_until IS NOT NULL AND p.premium_until > CURRENT_TIMESTAMP THEN 0
                ELSE 1
              END,
              p.premium_since DESC NULLS LAST,
              p.created_at DESC
            """,
            countQuery = """
            SELECT count(*)
            FROM performer_profiles p
            WHERE p.type = :type
              AND p.published = true
              AND (
                    (p.premium_until IS NOT NULL AND p.premium_until > CURRENT_TIMESTAMP)
                    OR (
                        (:city IS NULL OR p.city ILIKE CONCAT('%', :city, '%'))
                        AND (:minAge IS NULL OR p.age >= :minAge)
                        AND (:maxAge IS NULL OR p.age <= :maxAge)
                        AND (:gender IS NULL OR p.gender = :gender)
                        AND (:ethnicity IS NULL OR p.ethnicity = :ethnicity)
                        AND (:minRate IS NULL OR p.min_rate >= :minRate)
                        AND (:maxRate IS NULL OR p.min_rate <= :maxRate)
                        AND (:rateUnit IS NULL OR p.rate_unit = :rateUnit)
                    )
              )
            """,
            nativeQuery = true)
    Page<PerformerProfile> findActorsWithFilters(
            String type,
            String city,
            Integer minAge,
            Integer maxAge,
            String gender,
            String ethnicity,
            Double minRate,
            Double maxRate,
            String rateUnit,
            Pageable pageable
    );


    /* ================= CREATORS ================= */

    @Query(value = """
            SELECT *
            FROM performer_profiles p
            WHERE p.type = :type
              AND p.published = true
              AND (
                    (p.premium_until IS NOT NULL AND p.premium_until > CURRENT_TIMESTAMP)
                    OR (
                        (:city IS NULL OR p.city ILIKE CONCAT('%', :city, '%'))
                        AND (:activityType IS NULL OR p.activity_type = :activityType)
                    )
              )
            ORDER BY
              CASE
                WHEN p.premium_until IS NOT NULL AND p.premium_until > CURRENT_TIMESTAMP THEN 0
                ELSE 1
              END,
              p.premium_since DESC NULLS LAST,
              p.created_at DESC
            """,
            countQuery = """
            SELECT count(*)
            FROM performer_profiles p
            WHERE p.type = :type
              AND p.published = true
              AND (
                    (p.premium_until IS NOT NULL AND p.premium_until > CURRENT_TIMESTAMP)
                    OR (
                        (:city IS NULL OR p.city ILIKE CONCAT('%', :city, '%'))
                        AND (:activityType IS NULL OR p.activity_type = :activityType)
                    )
              )
            """,
            nativeQuery = true)
    Page<PerformerProfile> findCreatorsWithFilters(
            String type,
            String city,
            String activityType,
            Pageable pageable
    );


    /* ================= LOCATIONS ================= */

    @Query(value = """
            SELECT *
            FROM performer_profiles p
            WHERE p.type = :type
              AND p.published = true
              AND (
                    (p.premium_until IS NOT NULL AND p.premium_until > CURRENT_TIMESTAMP)
                    OR (
                        (:city IS NULL OR p.city ILIKE CONCAT('%', :city, '%'))
                        AND (:minPrice IS NULL OR p.rent_price >= :minPrice)
                        AND (:maxPrice IS NULL OR p.rent_price <= :maxPrice)
                    )
              )
            ORDER BY
              CASE
                WHEN p.premium_until IS NOT NULL AND p.premium_until > CURRENT_TIMESTAMP THEN 0
                ELSE 1
              END,
              p.premium_since DESC NULLS LAST,
              p.created_at DESC
            """,
            countQuery = """
            SELECT count(*)
            FROM performer_profiles p
            WHERE p.type = :type
              AND p.published = true
              AND (
                    (p.premium_until IS NOT NULL AND p.premium_until > CURRENT_TIMESTAMP)
                    OR (
                        (:city IS NULL OR p.city ILIKE CONCAT('%', :city, '%'))
                        AND (:minPrice IS NULL OR p.rent_price >= :minPrice)
                        AND (:maxPrice IS NULL OR p.rent_price <= :maxPrice)
                    )
              )
            """,
            nativeQuery = true)
    Page<PerformerProfile> findLocationsWithFilters(
            String type,
            String city,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );
}
