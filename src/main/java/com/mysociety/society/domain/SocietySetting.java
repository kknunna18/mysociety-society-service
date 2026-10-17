package com.mysociety.society.domain;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "society_settings", schema = "mysociety")
@Getter
@Setter
@NoArgsConstructor
public class SocietySetting extends TenantEntity {
    @Column(name = "setting_key", nullable = false)
    private String settingKey;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "setting_value", nullable = false, columnDefinition = "jsonb")
    private JsonNode settingValue;
    private String description;
    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom;
    @Column(name = "effective_to")
    private Instant effectiveTo;
    @Column(name = "created_by")
    private UUID createdBy;

    @PrePersist
    void defaultEffectiveFrom() {
        if (effectiveFrom == null) effectiveFrom = Instant.now();
    }
}
