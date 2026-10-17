/*
 * Society Service PostgreSQL schema
 *
 * Canonical source: https://github.com/kknunna18/identity-service/blob/main/mysociety_postgresql_complete.sql
 * Target: PostgreSQL 16+
 *
 * This script is safe to run against an empty database or re-run after a
 * successful execution. It owns only Society Service tables in mysociety.
 */

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE SCHEMA IF NOT EXISTS mysociety;
SET search_path TO mysociety, public;

CREATE TABLE IF NOT EXISTS societies (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                VARCHAR(30) NOT NULL UNIQUE,
    name                VARCHAR(150) NOT NULL,
    registration_number VARCHAR(80),
    email               VARCHAR(254),
    phone               VARCHAR(30),
    address_line1       VARCHAR(200),
    address_line2       VARCHAR(200),
    city                VARCHAR(100),
    state_name          VARCHAR(100),
    postal_code         VARCHAR(20),
    country_code        CHAR(2) NOT NULL DEFAULT 'IN',
    timezone            VARCHAR(60) NOT NULL DEFAULT 'Asia/Kolkata',
    currency_code       CHAR(3) NOT NULL DEFAULT 'INR',
    logo_url            VARCHAR(500),
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                        CHECK (status IN ('TRIAL','ACTIVE','SUSPENDED','INACTIVE')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version             BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS buildings (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id       UUID NOT NULL REFERENCES societies(id) ON DELETE CASCADE,
    code             VARCHAR(30) NOT NULL,
    name             VARCHAR(100) NOT NULL,
    number_of_floors INTEGER CHECK (number_of_floors >= 0),
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                     CHECK (status IN ('ACTIVE','INACTIVE','UNDER_MAINTENANCE')),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version          BIGINT NOT NULL DEFAULT 0,
    UNIQUE (society_id, code)
);

CREATE TABLE IF NOT EXISTS units (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id       UUID NOT NULL REFERENCES societies(id) ON DELETE CASCADE,
    building_id      UUID NOT NULL REFERENCES buildings(id) ON DELETE RESTRICT,
    unit_number      VARCHAR(30) NOT NULL,
    floor_number     INTEGER,
    unit_type        VARCHAR(30),
    area_sq_ft       NUMERIC(12,2) CHECK (area_sq_ft IS NULL OR area_sq_ft > 0),
    occupancy_status VARCHAR(20) NOT NULL DEFAULT 'VACANT'
                     CHECK (occupancy_status IN ('VACANT','OWNER_OCCUPIED','TENANT_OCCUPIED','UNAVAILABLE')),
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                     CHECK (status IN ('ACTIVE','INACTIVE')),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version          BIGINT NOT NULL DEFAULT 0,
    UNIQUE (society_id, building_id, unit_number),
    UNIQUE (society_id, id)
);

CREATE INDEX IF NOT EXISTS ix_units_society_building
    ON units (society_id, building_id, status);

CREATE TABLE IF NOT EXISTS household_memberships (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id             UUID NOT NULL REFERENCES societies(id) ON DELETE CASCADE,
    unit_id                UUID NOT NULL REFERENCES units(id) ON DELETE CASCADE,
    user_id                UUID NOT NULL,
    membership_type        VARCHAR(20) NOT NULL
                           CHECK (membership_type IN ('OWNER','TENANT','FAMILY_MEMBER','CARETAKER')),
    is_primary_contact     BOOLEAN NOT NULL DEFAULT FALSE,
    move_in_date           DATE NOT NULL,
    move_out_date          DATE,
    verification_status    VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                           CHECK (verification_status IN ('PENDING','VERIFIED','REJECTED')),
    emergency_contact_name VARCHAR(150),
    emergency_contact_phone VARCHAR(30),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version                BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_membership_dates CHECK (move_out_date IS NULL OR move_out_date >= move_in_date),
    UNIQUE (society_id, unit_id, user_id, move_in_date)
);

CREATE INDEX IF NOT EXISTS ix_membership_user_active
    ON household_memberships (user_id, society_id, move_out_date);
CREATE INDEX IF NOT EXISTS ix_membership_unit_active
    ON household_memberships (society_id, unit_id, move_out_date);
CREATE UNIQUE INDEX IF NOT EXISTS ux_membership_primary_active
    ON household_memberships (society_id, unit_id)
    WHERE is_primary_contact = TRUE AND move_out_date IS NULL;

CREATE TABLE IF NOT EXISTS vehicles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id      UUID NOT NULL REFERENCES societies(id) ON DELETE CASCADE,
    unit_id         UUID NOT NULL REFERENCES units(id) ON DELETE CASCADE,
    owner_user_id   UUID,
    registration_no VARCHAR(30) NOT NULL,
    vehicle_type    VARCHAR(20) NOT NULL
                    CHECK (vehicle_type IN ('TWO_WHEELER','CAR','BICYCLE','OTHER')),
    make_model      VARCHAR(100),
    color           VARCHAR(50),
    parking_slot    VARCHAR(50),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version         BIGINT NOT NULL DEFAULT 0,
    UNIQUE (society_id, registration_no)
);

CREATE TABLE IF NOT EXISTS society_settings (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id     UUID NOT NULL REFERENCES societies(id) ON DELETE CASCADE,
    setting_key    VARCHAR(100) NOT NULL,
    setting_value  JSONB NOT NULL,
    description    VARCHAR(500),
    effective_from TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    effective_to   TIMESTAMPTZ,
    created_by     UUID,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version        BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_setting_dates CHECK (effective_to IS NULL OR effective_to > effective_from),
    UNIQUE (society_id, setting_key, effective_from)
);
