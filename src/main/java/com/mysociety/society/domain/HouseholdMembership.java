package com.mysociety.society.domain;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "household_memberships", schema = "mysociety")
@Getter
@Setter
@NoArgsConstructor
public class HouseholdMembership extends TenantEntity {
    @Column(name = "unit_id", nullable = false)
    private UUID unitId;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "membership_type", nullable = false)
    private String membershipType;
    @Column(name = "is_primary_contact", nullable = false)
    private boolean primaryContact;
    @Column(name = "move_in_date", nullable = false)
    private LocalDate moveInDate;
    @Column(name = "move_out_date")
    private LocalDate moveOutDate;
    @Column(name = "verification_status", nullable = false)
    private String verificationStatus = "PENDING";
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
}
