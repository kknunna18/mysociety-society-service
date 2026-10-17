package com.mysociety.society.web;

import com.mysociety.society.domain.Society;
import com.mysociety.society.repository.SocietyRepository;
import com.mysociety.society.tenant.TenantContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/societies")
public class SocietyController {
    private final SocietyRepository repo;

    public SocietyController(SocietyRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/current")
    public SocietyResponse current() {
        return response(repo.findById(TenantContext.societyId()).orElseThrow(() -> new NoSuchElementException("Society not found")));
    }

    /** Societies the caller belongs to. */
    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_SOCIETY_VIEW')")
    @Transactional(readOnly = true)
    public Page<SocietyResponse> list(@RequestParam(defaultValue = "") String q,
                                      @RequestParam(defaultValue = "") String status,
                                      @RequestParam(defaultValue = "0") @Min(0) int page,
                                      @RequestParam(defaultValue = "12") @Min(1) @Max(100) int size) {
        return repo.findForUser(TenantContext.userId(), q.trim(), status.trim(), PageRequest.of(page, size)).map(this::response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_SOCIETY_VIEW')")
    @Transactional(readOnly = true)
    public SocietyResponse get(@PathVariable UUID id) {
        return response(accessible(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PERMISSION_SOCIETY_MANAGE')")
    @Transactional
    public SocietyResponse create(@Valid @RequestBody SocietyRequest r) {
        if (repo.existsByCodeIgnoreCase(r.code().trim())) throw new IllegalArgumentException("Society code already exists");
        Society s = new Society();
        apply(s, r);
        s = repo.saveAndFlush(s);
        repo.grantAdmin(TenantContext.userId(), s.getId());
        return response(s);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_SOCIETY_MANAGE')")
    @Transactional
    public SocietyResponse update(@PathVariable UUID id, @Valid @RequestBody SocietyRequest r) {
        Society s = accessible(id);
        if (repo.existsByCodeIgnoreCaseAndIdNot(r.code().trim(), id)) throw new IllegalArgumentException("Society code already exists");
        apply(s, r);
        return response(repo.save(s));
    }

    private Society accessible(UUID id) {
        if (!repo.isMember(TenantContext.userId(), id)) throw new NoSuchElementException("Society not found");
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Society not found"));
    }

    private void apply(Society s, SocietyRequest r) {
        s.setCode(r.code().trim().toUpperCase());
        s.setName(r.name().trim());
        s.setRegistrationNumber(blank(r.registrationNumber()));
        s.setEmail(blank(r.email()));
        s.setPhone(blank(r.phone()));
        s.setAddressLine1(blank(r.addressLine1()));
        s.setAddressLine2(blank(r.addressLine2()));
        s.setCity(blank(r.city()));
        s.setStateName(blank(r.stateName()));
        s.setPostalCode(blank(r.postalCode()));
        if (r.countryCode() != null) s.setCountryCode(r.countryCode());
        if (r.timezone() != null) s.setTimezone(r.timezone());
        if (r.currencyCode() != null) s.setCurrencyCode(r.currencyCode());
        s.setLogoUrl(blank(r.logoUrl()));
        if (r.status() != null) s.setStatus(r.status());
    }

    private static String blank(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }

    private SocietyResponse response(Society s) {
        return new SocietyResponse(s.getId(), s.getCode(), s.getName(), s.getRegistrationNumber(), s.getEmail(), s.getPhone(),
                s.getAddressLine1(), s.getAddressLine2(), s.getCity(), s.getStateName(), s.getPostalCode(), s.getCountryCode(),
                s.getTimezone(), s.getCurrencyCode(), s.getLogoUrl(), s.getStatus(), s.getCreatedAt(), s.getUpdatedAt(), s.getVersion());
    }

    record SocietyRequest(@NotBlank @Size(max = 30) String code, @NotBlank @Size(max = 150) String name,
                          @Size(max = 80) String registrationNumber,
                          @Email @Size(max = 254) String email, @Size(max = 30) String phone,
                          @Size(max = 200) String addressLine1, @Size(max = 200) String addressLine2,
                          @Size(max = 100) String city, @Size(max = 100) String stateName,
                          @Size(max = 20) String postalCode, @Size(min = 2, max = 2) String countryCode,
                          @Size(max = 60) String timezone, @Size(min = 3, max = 3) String currencyCode,
                          @Size(max = 500) String logoUrl,
                          @Pattern(regexp = "TRIAL|ACTIVE|SUSPENDED|INACTIVE") String status) {
    }

    record SocietyResponse(UUID id, String code, String name, String registrationNumber, String email, String phone,
                           String addressLine1, String addressLine2, String city, String stateName, String postalCode,
                           String countryCode, String timezone, String currencyCode, String logoUrl, String status,
                           Instant createdAt, Instant updatedAt, long version) {
    }
}
