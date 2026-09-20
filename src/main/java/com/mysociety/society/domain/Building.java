package com.mysociety.society.domain;

import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="buildings", schema="mysociety") @Getter @Setter @NoArgsConstructor
public class Building extends TenantEntity {
    @Column(nullable=false) private String code; @Column(nullable=false) private String name;
    @Column(name="number_of_floors") private Integer numberOfFloors;
    @Column(nullable=false) private String status = "ACTIVE";
}
