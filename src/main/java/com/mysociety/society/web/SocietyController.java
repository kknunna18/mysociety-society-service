package com.mysociety.society.web;

import com.mysociety.society.domain.Society;
import com.mysociety.society.repository.SocietyRepository;
import com.mysociety.society.tenant.TenantContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.*;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public SocietyResponse create(@Valid @RequestBody SocietyRequest r) {
        Society s = new Society();
        apply(s, r);
        return response(repo.save(s));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public SocietyResponse update(@PathVariable UUID id, @Valid @RequestBody SocietyRequest r) {
        if (!id.equals(TenantContext.societyId())) throw new NoSuchElementException("Society not found");
        Society s = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Society not found"));
        apply(s, r);
        return response(repo.save(s));
    }

    private void apply(Society s, SocietyRequest r) {
        s.setCode(r.code());
        s.setName(r.name());
        s.setEmail(r.email());
        s.setPhone(r.phone());
        s.setCity(r.city());
        s.setStatus(r.status());
    }

    private SocietyResponse response(Society s) {
        return new SocietyResponse(s.getId(), s.getCode(), s.getName(), s.getEmail(), s.getPhone(), s.getCity(), s.getStatus());
    }

    record SocietyRequest(@NotBlank @Size(max = 30) String code, @NotBlank @Size(max = 150) String name,
                          @Email String email, @Size(max = 30) String phone, @Size(max = 100) String city,
                          @Pattern(regexp = "TRIAL|ACTIVE|SUSPENDED|INACTIVE") String status) {
    }

    record SocietyResponse(UUID id, String code, String name, String email, String phone, String city, String status) {
    }
}
