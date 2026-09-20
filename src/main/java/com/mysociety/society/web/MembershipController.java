package com.mysociety.society.web;
import com.mysociety.society.domain.HouseholdMembership; import com.mysociety.society.outbox.*; import com.mysociety.society.repository.*; import com.mysociety.society.tenant.TenantContext;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.*; import java.util.*; import org.slf4j.MDC; import org.springframework.data.domain.*; import org.springframework.data.web.PageableDefault; import org.springframework.http.*; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/residents/memberships") public class MembershipController {
 private final MembershipRepository repo; private final UnitRepository units; private final TransactionalOutbox outbox;
 public MembershipController(MembershipRepository repo,UnitRepository units,TransactionalOutbox outbox){this.repo=repo;this.units=units;this.outbox=outbox;}
 @GetMapping public Page<Response> list(@PageableDefault(size=20) Pageable p){return repo.findBySocietyId(TenantContext.societyId(),p).map(this::out);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @Transactional public Response create(@Valid @RequestBody Request r){ensureUnit(r.unitId());HouseholdMembership m=new HouseholdMembership();m.setSocietyId(TenantContext.societyId());apply(m,r);m=repo.save(m);event("membership.created",m);return out(m);}
 @GetMapping("/{id}") public Response get(@PathVariable UUID id){return out(find(id));}
 @PutMapping("/{id}") @Transactional public Response update(@PathVariable UUID id,@Valid @RequestBody Request r){ensureUnit(r.unitId());HouseholdMembership m=find(id);apply(m,r);m=repo.save(m);event("membership.updated",m);return out(m);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Transactional public void delete(@PathVariable UUID id){HouseholdMembership m=find(id);repo.delete(m);event("membership.deleted",m);}
 private void ensureUnit(UUID id){if(units.findByIdAndSocietyId(id,TenantContext.societyId()).isEmpty())throw new IllegalArgumentException("unitId does not belong to this society");}
 private HouseholdMembership find(UUID id){return repo.findByIdAndSocietyId(id,TenantContext.societyId()).orElseThrow(()->new NoSuchElementException("Membership not found"));}
 private void apply(HouseholdMembership m,Request r){m.setUnitId(r.unitId());m.setUserId(r.userId());m.setMembershipType(r.membershipType());m.setPrimaryContact(r.primaryContact());m.setMoveInDate(r.moveInDate());m.setMoveOutDate(r.moveOutDate());m.setVerificationStatus(r.verificationStatus());}
 private void event(String type,HouseholdMembership m){outbox.enqueue(new DomainEvent(UUID.randomUUID(),type,1,Instant.now(),m.getSocietyId(),"household_membership",m.getId(),MDC.get(CorrelationIdFilter.HEADER),Map.of("userId",m.getUserId(),"unitId",m.getUnitId())));}
 private Response out(HouseholdMembership m){return new Response(m.getId(),m.getUnitId(),m.getUserId(),m.getMembershipType(),m.isPrimaryContact(),m.getMoveInDate(),m.getMoveOutDate(),m.getVerificationStatus());}
 record Request(@NotNull UUID unitId,@NotNull UUID userId,@Pattern(regexp="OWNER|TENANT|FAMILY_MEMBER|CARETAKER") String membershipType,boolean primaryContact,@NotNull LocalDate moveInDate,LocalDate moveOutDate,@Pattern(regexp="PENDING|VERIFIED|REJECTED") String verificationStatus){}
 record Response(UUID id,UUID unitId,UUID userId,String membershipType,boolean primaryContact,LocalDate moveInDate,LocalDate moveOutDate,String verificationStatus){}
}
