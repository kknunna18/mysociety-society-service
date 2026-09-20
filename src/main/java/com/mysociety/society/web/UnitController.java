package com.mysociety.society.web;
import com.mysociety.society.domain.Unit; import com.mysociety.society.repository.*; import com.mysociety.society.tenant.TenantContext;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.web.PageableDefault; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/units") public class UnitController {
 private final UnitRepository repo; private final BuildingRepository buildings; public UnitController(UnitRepository repo,BuildingRepository buildings){this.repo=repo;this.buildings=buildings;}
 @GetMapping public Page<Response> list(@PageableDefault(size=20,sort="unitNumber") Pageable p){return repo.findBySocietyId(TenantContext.societyId(),p).map(this::out);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Response create(@Valid @RequestBody Request r){ensureBuilding(r.buildingId()); Unit u=new Unit();u.setSocietyId(TenantContext.societyId());apply(u,r);return out(repo.save(u));}
 @GetMapping("/{id}") public Response get(@PathVariable UUID id){return out(find(id));}
 @PutMapping("/{id}") public Response update(@PathVariable UUID id,@Valid @RequestBody Request r){ensureBuilding(r.buildingId());Unit u=find(id);apply(u,r);return out(repo.save(u));}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id){repo.delete(find(id));}
 private void ensureBuilding(UUID id){if(buildings.findByIdAndSocietyId(id,TenantContext.societyId()).isEmpty())throw new IllegalArgumentException("buildingId does not belong to this society");}
 private Unit find(UUID id){return repo.findByIdAndSocietyId(id,TenantContext.societyId()).orElseThrow(()->new NoSuchElementException("Unit not found"));}
 private void apply(Unit u,Request r){u.setBuildingId(r.buildingId());u.setUnitNumber(r.unitNumber());u.setFloorNumber(r.floorNumber());u.setUnitType(r.unitType());u.setAreaSqFt(r.areaSqFt());u.setOccupancyStatus(r.occupancyStatus());u.setStatus(r.status());}
 private Response out(Unit u){return new Response(u.getId(),u.getBuildingId(),u.getUnitNumber(),u.getFloorNumber(),u.getUnitType(),u.getAreaSqFt(),u.getOccupancyStatus(),u.getStatus());}
 record Request(@NotNull UUID buildingId,@NotBlank @Size(max=30) String unitNumber,Integer floorNumber,@Size(max=30) String unitType,@DecimalMin("0.01") BigDecimal areaSqFt,@Pattern(regexp="VACANT|OWNER_OCCUPIED|TENANT_OCCUPIED|UNAVAILABLE") String occupancyStatus,@Pattern(regexp="ACTIVE|INACTIVE") String status){}
 record Response(UUID id,UUID buildingId,String unitNumber,Integer floorNumber,String unitType,BigDecimal areaSqFt,String occupancyStatus,String status){}
}
