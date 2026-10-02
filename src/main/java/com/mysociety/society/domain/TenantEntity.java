package com.mysociety.society.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class TenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;
    @Column(name = "society_id", nullable = false, updatable = false)
    protected UUID societyId;
    @Column(name = "created_at", nullable = false, updatable = false)
    protected Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    protected Instant updatedAt;
    @Version
    protected long version;

    @PrePersist
    void created() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void updated() {
        updatedAt = Instant.now();
    }
}
