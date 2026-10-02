package com.mysociety.society.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "societies", schema = "mysociety")
@Getter
@Setter
@NoArgsConstructor
public class Society {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true, length = 30)
    private String code;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(name = "registration_number")
    private String registrationNumber;
    private String email;
    private String phone;
    @Column(name = "address_line1")
    private String addressLine1;
    @Column(name = "address_line2")
    private String addressLine2;
    private String city;
    @Column(name = "state_name")
    private String stateName;
    @Column(name = "postal_code")
    private String postalCode;
    @Column(name = "country_code")
    private String countryCode = "IN";
    private String timezone = "Asia/Kolkata";
    @Column(name = "currency_code")
    private String currencyCode = "INR";
    @Column(name = "logo_url")
    private String logoUrl;
    @Column(nullable = false)
    private String status = "ACTIVE";
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @Version
    private long version;

    @PrePersist
    void created() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void updated() {
        updatedAt = Instant.now();
    }
}
