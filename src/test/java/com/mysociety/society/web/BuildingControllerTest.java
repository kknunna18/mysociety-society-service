package com.mysociety.society.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mysociety.society.domain.Building;
import com.mysociety.society.repository.BuildingRepository;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class BuildingControllerTest {
    private MockMvc mvc;
    private BuildingRepository repo;
    private UUID societyId;

    @BeforeEach
    void setUp() {
        repo = mock(BuildingRepository.class);
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        mvc = MockMvcBuilders.standaloneSetup(new BuildingController(repo)).setControllerAdvice(new ApiExceptionHandler()).build();
        societyId = UUID.randomUUID();
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(60), java.util.Map.of("alg", "HS256"), java.util.Map.of("sub", UUID.randomUUID().toString(), "society_id", societyId.toString()));
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rejectsInvalidBuildingRequest() throws Exception {
        mvc.perform(post("/buildings").contentType(MediaType.APPLICATION_JSON).content("{\"code\":\"\",\"name\":\"\"}")).andExpect(status().isBadRequest());
    }

    @Test
    void doesNotReadAnotherTenantsBuilding() throws Exception {
        UUID foreignBuilding = UUID.randomUUID();
        mvc.perform(get("/buildings/{id}", foreignBuilding)).andExpect(status().isNotFound());
        verify(repo).findByIdAndSocietyId(foreignBuilding, societyId);
    }
}
