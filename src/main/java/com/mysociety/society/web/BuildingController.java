package com.mysociety.society.web;
import com.mysociety.society.domain.Building; import com.mysociety.society.repository.BuildingRepository; import com.mysociety.society.tenant.TenantContext;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.web.PageableDefault; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/buildings") public class BuildingController {
 private final BuildingRepository repo; public BuildingController(BuildingRepository repo){this.repo=repo;}
 @GetMapping public Page<Response> list(@PageableDefault(size=20,sort="name") Pageable page){return repo.findBySocietyId(TenantContext.societyId(),page).map(this::out);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Response create(@Valid @RequestBody Request r){Building b=new Building();b.setSocietyId(TenantContext.societyId());apply(b,r);return out(repo.save(b));}
 @GetMapping("/{id}") public Response get(@PathVariable UUID id){return out(find(id));}
 @PutMapping("/{id}") public Response update(@PathVariable UUID id,@Valid @RequestBody Request r){Building b=find(id);apply(b,r);return out(repo.save(b));}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id){repo.delete(find(id));}
 private Building find(UUID id){return repo.findByIdAndSocietyId(id,TenantContext.societyId()).orElseThrow(()->new NoSuchElementException("Building not found"));}
 private void apply(Building b,Request r){b.setCode(r.code());b.setName(r.name());b.setNumberOfFloors(r.numberOfFloors());b.setStatus(r.status());}
 private Response out(Building b){return new Response(b.getId(),b.getCode(),b.getName(),b.getNumberOfFloors(),b.getStatus());}
 record Request(@NotBlank @Size(max=30) String code,@NotBlank @Size(max=100) String name,@Min(0) Integer numberOfFloors,@Pattern(regexp="ACTIVE|INACTIVE|UNDER_MAINTENANCE") String status){}
 record Response(UUID id,String code,String name,Integer numberOfFloors,String status){}
}
