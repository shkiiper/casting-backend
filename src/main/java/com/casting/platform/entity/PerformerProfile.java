package com.casting.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "performer_profiles")
@Getter
@Setter
public class PerformerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerformerType type;

    // =====================
    // BASIC
    // =====================

    private String firstName;
    private String lastName;
    private String displayName;

    @Column(name = "city", length = 255)
    private String city;

    private String mainPhotoUrl;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String bio;

    // =====================
    // ACTOR
    // =====================

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;
    private String ethnicity;
    private Integer heightCm;
    private Integer weightKg;
    private String bodyType;
    private String hairColor;
    private String eyeColor;
    private Integer gameAgeFrom;
    private Integer gameAgeTo;
    @Column(columnDefinition = "TEXT")
    private String skillsJson;
    @Column(columnDefinition = "TEXT")
    private String introVideoUrl;
    @Column(columnDefinition = "TEXT")
    private String monologueVideoUrl;
    @Column(columnDefinition = "TEXT")
    private String selfTapeVideoUrl;

    @Column(columnDefinition = "TEXT")
    private String experienceText;
    private BigDecimal minRate;
    private String rateUnit; // HOUR / DAY / PROJECT

    // =====================
    // CREATOR
    // =====================

    private String activityType;
    @Column(columnDefinition = "TEXT")
    private String activityTypesJson;
    private String experienceLevel;
    @Column(columnDefinition = "TEXT")
    private String projectFormatsJson;
    @Column(columnDefinition = "TEXT")
    private String achievements;

    // =====================
    // LOCATION
    // =====================

    private String locationName;
    private String address;
    private BigDecimal rentPrice;
    private String rentPriceUnit;
    private Integer floor;
    private String locationType;
    private LocalTime availabilityFrom;
    private LocalTime availabilityTo;
    @Column(columnDefinition = "TEXT")
    private String rentalTerms;
    @Column(columnDefinition = "TEXT")
    private String extraConditions;

    // =====================
    // CONTACTS
    // =====================

    private String contactPhone;
    private String contactEmail;
    private String contactWhatsapp;
    private String contactTelegram;
    private String contactInstagram;
    @Column(columnDefinition = "TEXT")
    private String socialLinksJson;

    // =====================
    // MEDIA (FIXED)
    // =====================

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "profile_photos",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "photo_url")
    private Set<String> photoUrls = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "creator_portfolio_photos",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "photo_url")
    private Set<String> portfolioPhotoUrls = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "profile_videos",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "video_url")
    private Set<String> videoUrls = new LinkedHashSet<>();


    // =====================
    // STATUS
    // =====================

    private boolean published = false;

    private LocalDateTime premiumSince;
    private LocalDateTime premiumUntil;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
