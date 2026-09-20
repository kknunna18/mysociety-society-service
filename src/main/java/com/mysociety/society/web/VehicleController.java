package com.mysociety.society.web;
import com.mysociety.society.domain.Vehicle; import com.mysociety.society.repository.*; import com.mysociety.society.tenant.TenantContext; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.web.PageableDefault; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/residents/vehicles") public class VehicleController {
 private final VehicleRepository repo; private final UnitRepository units; public VehicleController(VehicleRepository repo,UnitRepository units){this.repo=repo;this.units=units;}
 @GetMapping public Page<Response> list(@PageableDefault(size=20) Pageable p){return repo.findBySocietyId(TenantContext.societyId(),p).map(this::out);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Response create(@Valid @RequestBody Request r){ensureUnit(r.unitId());Vehicle v=new Vehicle();v.setSocietyId(TenantContext.societyId());apply(v,r);return out(repo.save(v));}
 @GetMapping("/{id}") public Response get(@PathVariable UUID id){return out(find(id));}
 @PutMapping("/{id}") public Response update(@PathVariable UUID id,@Valid @RequestBody Request r){ensureUnit(r.unitId());Vehicle v=find(id);apply(v,r);return out(repo.save(v));}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id){repo.delete(find(id));}
 private void ensureUnit(UUID id){if(units.findByIdAndSocietyId(id,TenantContext.societyId()).isEmpty())throw new IllegalArgumentException("unitId does not belong to this society");}
 private Vehicle find(UUID id){return repo.findByIdAndSocietyId(id,TenantContext.societyId()).orElseThrow(()->new NoSuchElementException("Vehicle not found"));}
 private void apply(Vehicle v,Request r){v.setUnitId(r.unitId());v.setOwnerUserId(r.ownerUserId());v.setRegistrationNo(r.registrationNo());v.setVehicleType(r.vehicleType());v.setMakeModel(r.makeModel());v.setColor(r.color());v.setParkingSlot(r.parkingSlot());v.setActive(r.active());}
 private Response out(Vehicle v){return new Response(v.getId(),v.getUnitId(),v.getOwnerUserId(),v.getRegistrationNo(),v.getVehicleType(),v.getMakeModel(),v.getColor(),v.getParkingSlot(),v.isActive());}
 record Request(@NotNull UUID unitId,UUID ownerUserId,@NotBlank @Size(max=30) String registrationNo,@Pattern(regexp="TWO_WHEELER|CAR|BICYCLE|OTHER") String vehicleType,@Size(max=100) String makeModel,@Size(max=50) String color,@Size(max=50) String parkingSlot,boolean active){}
 record Response(UUID id,UUID unitId,UUID ownerUserId,String registrationNo,String vehicleType,String makeModel,String color,String parkingSlot,boolean active){}
}
