package com.casting.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phone")
})
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private String city;
    private String description;
    private String telegram;


    private boolean emailVerified = false;
    private boolean phoneVerified = false;
    private boolean active = true;
    private boolean banned = false;

    // Для исполнителей (ACTOR, CREATOR, LOCATION_OWNER)
    private boolean premium = false;
    private LocalDateTime premiumUntil;

    // Для заказчиков (CUSTOMER)
    private boolean customerSubscriptionActive = false;
    private LocalDateTime customerSubscriptionUntil;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private PerformerProfile performerProfile;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<CustomerSubscription> subscriptions;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<ContactView> contactViews;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<CastingPost> castingPosts;
}
