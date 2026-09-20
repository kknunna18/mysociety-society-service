package com.mysociety.society.web;
import com.fasterxml.jackson.databind.JsonNode; import com.mysociety.society.domain.SocietySetting; import com.mysociety.society.repository.SettingRepository; import com.mysociety.society.tenant.TenantContext; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.*; import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.web.PageableDefault; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/societies/settings") public class SettingController {
 private final SettingRepository repo; public SettingController(SettingRepository repo){this.repo=repo;}
 @GetMapping public Page<Response> list(@PageableDefault(size=20,sort="settingKey") Pageable p){return repo.findBySocietyId(TenantContext.societyId(),p).map(this::out);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Response create(@Valid @RequestBody Request r){SocietySetting s=new SocietySetting();s.setSocietyId(TenantContext.societyId());apply(s,r);s.setCreatedBy(TenantContext.userId());return out(repo.save(s));}
 @GetMapping("/{id}") public Response get(@PathVariable UUID id){return out(find(id));}
 @PutMapping("/{id}") public Response update(@PathVariable UUID id,@Valid @RequestBody Request r){SocietySetting s=find(id);apply(s,r);return out(repo.save(s));}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id){repo.delete(find(id));}
 private SocietySetting find(UUID id){return repo.findByIdAndSocietyId(id,TenantContext.societyId()).orElseThrow(()->new NoSuchElementException("Setting not found"));}
 private void apply(SocietySetting s,Request r){s.setSettingKey(r.settingKey());s.setSettingValue(r.settingValue());s.setDescription(r.description());s.setEffectiveFrom(r.effectiveFrom());s.setEffectiveTo(r.effectiveTo());}
 private Response out(SocietySetting s){return new Response(s.getId(),s.getSettingKey(),s.getSettingValue(),s.getDescription(),s.getEffectiveFrom(),s.getEffectiveTo());}
 record Request(@NotBlank @Size(max=100) String settingKey,@NotNull JsonNode settingValue,@Size(max=500) String description,Instant effectiveFrom,Instant effectiveTo){}
 record Response(UUID id,String settingKey,JsonNode settingValue,String description,Instant effectiveFrom,Instant effectiveTo){}
}
