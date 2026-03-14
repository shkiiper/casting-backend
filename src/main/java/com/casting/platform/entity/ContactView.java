package com.casting.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_views",
        uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "profile_id"}))
@Getter
@Setter
public class ContactView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private PerformerProfile profile;

    @CreationTimestamp
    private LocalDateTime viewedAt;
}
