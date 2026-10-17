package com.mysociety.society.domain;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "units", schema = "mysociety")
@Getter
@Setter
@NoArgsConstructor
public class Unit extends TenantEntity {
    @Column(name = "building_id", nullable = false)
    private UUID buildingId;
    @Column(name = "unit_number", nullable = false)
    private String unitNumber;
    @Column(name = "floor_number")
    private Integer floorNumber;
    @Column(name = "unit_type")
    private String unitType;
    @Column(name = "area_sq_ft")
    private BigDecimal areaSqFt;
    @Column(name = "occupancy_status", nullable = false)
    private String occupancyStatus = "VACANT";
    @Column(nullable = false)
    private String status = "ACTIVE";
}
