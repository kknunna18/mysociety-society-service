package com.mysociety.society.domain;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles", schema = "mysociety")
@Getter
@Setter
@NoArgsConstructor
public class Vehicle extends TenantEntity {
    @Column(name = "unit_id", nullable = false)
    private UUID unitId;
    @Column(name = "owner_user_id")
    private UUID ownerUserId;
    @Column(name = "registration_no", nullable = false)
    private String registrationNo;
    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;
    @Column(name = "make_model")
    private String makeModel;
    private String color;
    @Column(name = "parking_slot")
    private String parkingSlot;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
