package com.mysociety.society.repository;

import com.mysociety.society.domain.Society;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SocietyRepository extends JpaRepository<Society, UUID> {
    Optional<Society> findById(UUID id);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);

    /** Societies the user holds an active role in, optionally filtered by free text and status ('' = any). */
    @Query(value = "select s.* from mysociety.societies s where s.id in (select r.society_id from mysociety.user_society_roles r"
            + " where r.user_id = :userId and r.is_active = true and (r.valid_until is null or r.valid_until >= current_date))"
            + " and (:status = '' or s.status = :status)"
            + " and (:q = '' or lower(s.name) like lower(concat('%', :q, '%')) or lower(s.code) like lower(concat('%', :q, '%'))"
            + " or lower(coalesce(s.city, '')) like lower(concat('%', :q, '%'))) order by s.name",
            countQuery = "select count(*) from mysociety.societies s where s.id in (select r.society_id from mysociety.user_society_roles r"
                    + " where r.user_id = :userId and r.is_active = true and (r.valid_until is null or r.valid_until >= current_date))"
                    + " and (:status = '' or s.status = :status)"
                    + " and (:q = '' or lower(s.name) like lower(concat('%', :q, '%')) or lower(s.code) like lower(concat('%', :q, '%'))"
                    + " or lower(coalesce(s.city, '')) like lower(concat('%', :q, '%')))",
            nativeQuery = true)
    Page<Society> findForUser(@Param("userId") UUID userId, @Param("q") String q, @Param("status") String status, Pageable pageable);

    @Query(value = "select count(*) > 0 from mysociety.user_society_roles r where r.user_id = :userId and r.society_id = :societyId"
            + " and r.is_active = true and (r.valid_until is null or r.valid_until >= current_date)", nativeQuery = true)
    boolean isMember(@Param("userId") UUID userId, @Param("societyId") UUID societyId);

    @Modifying
    @Query(value = "insert into mysociety.user_society_roles (society_id, user_id, role_id, is_active)"
            + " select :societyId, :userId, r.id, true from mysociety.roles r where r.code = 'SOCIETY_ADMIN' and r.society_id is null", nativeQuery = true)
    int grantAdmin(@Param("userId") UUID userId, @Param("societyId") UUID societyId);
}
