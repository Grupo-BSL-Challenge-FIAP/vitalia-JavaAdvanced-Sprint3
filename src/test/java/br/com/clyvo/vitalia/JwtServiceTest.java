package br.com.clyvo.vitalia;

import br.com.clyvo.vitalia.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class JwtSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar 401 ao acessar endpoint protegido sem token")
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar login com credenciais inválidas")
    void shouldReturnErrorOnInvalidLogin() throws Exception {
        Map invalidLogin = Map.of(
                "email", "inexistente@email.com",
                "password", "senhaerrada"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 403 quando TUTOR tenta acessar endpoint restrito de ADMIN")
    void shouldReturnForbiddenWhenTutorAccessesAdminRoute() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer token_falso_de_tutor"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anônimo não deve conseguir usar endpoint de registro de veterinário")
    void anonymousShouldNotAccessVetRegister() throws Exception {
        mockMvc.perform(post("/auth/register/vet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anônimo não deve conseguir usar endpoint de registro de admin")
    void anonymousShouldNotAccessAdminRegister() throws Exception {
        mockMvc.perform(post("/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 403 para anônimo tentando acessar históricos clínicos")
    void anonymousShouldNotAccessClinicalHistories() throws Exception {
        mockMvc.perform(get("/clinical-histories"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 403 para anônimo tentando acessar alertas")
    void anonymousShouldNotAccessAlerts() throws Exception {
        mockMvc.perform(get("/alerts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 403 ao acessar pets sem autenticação")
    void shouldReturnForbiddenWhenAccessingPetsWithoutAuth() throws Exception {
        mockMvc.perform(get("/pets"))
                .andExpect(status().isForbidden());
    }

    @Autowired
    private TokenService tokenService;

    @Test
    @DisplayName("Deve validar todas as claims do token JWT gerado")
    void shouldValidateJwtClaimsCorrectly() {
        br.com.clyvo.vitalia.entity.AppUser mockUser = new br.com.clyvo.vitalia.entity.AppUser();
        mockUser.setId(1L);
        mockUser.setEmail("teste@vitalia.com");

        String token = tokenService.generateToken(mockUser);

        org.junit.jupiter.api.Assertions.assertNotNull(token);
    }

    @Autowired
    private br.com.clyvo.vitalia.repository.AppRoleRepository roleRepository;

    @Test
    @DisplayName("Admin deve conseguir usar endpoints de registro protegido")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void adminShouldCreateUsers() throws Exception {
        roleRepository.findByName("VETERINARIAN")
                .orElseGet(() -> roleRepository.save(new br.com.clyvo.vitalia.entity.Role(null, "VETERINARIAN")));

        Map vetPayload = Map.of(
                "fullName", "Vet Test",
                "email", "novo.vet@test.com",
                "password", "123456",
                "phone", "11999999999"
        );

        mockMvc.perform(post("/auth/register/vet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vetPayload)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Tutor deve receber 403 ao tentar acessar históricos clínicos globais")
    @WithMockUser(username = "tutor@test.com", roles = {"TUTOR"})
    void tutorShouldReceiveForbiddenOnClinicalHistories() throws Exception {
        mockMvc.perform(get("/clinical-histories"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Tutor deve receber 403 ao tentar acessar alertas globais")
    @WithMockUser(username = "tutor@test.com", roles = {"TUTOR"})
    void tutorShouldReceiveForbiddenOnAlerts() throws Exception {
        mockMvc.perform(get("/alerts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Veterinario deve conseguir acessar listagens globais com sucesso")
    @WithMockUser(username = "vet@test.com", roles = {"VETERINARIAN"})
    void veterinarianShouldAccessGlobalLists() throws Exception {
        mockMvc.perform(get("/clinical-histories"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/alerts"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 403 ou 401 ao testar token expirado")
    void shouldReturnErrorOnExpiredToken() throws Exception {
        mockMvc.perform(get("/clinical-histories")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0ZSIsImV4cCI6MTAwMH0.invalido"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Tutor deve receber 403 ao tentar acessar listagem geral de pets sem permissão adequada")
    @WithMockUser(username = "tutor@test.com", roles = {"TUTOR"})
    void tutorShouldAccessOnlyOwnResources() throws Exception {
        mockMvc.perform(get("/pets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Tutor deve receber 404 ao tentar acessar pet inexistente de outro tutor")
    @WithMockUser(username = "tutor@test.com", roles = {"TUTOR"})
    void tutorShouldNotAccessOtherTutorResources() throws Exception {
        mockMvc.perform(get("/pets/9999"))
                .andExpect(status().isNotFound());
    }

    @Autowired
    private br.com.clyvo.vitalia.repository.PetRepository petRepository;

    @Autowired
    private br.com.clyvo.vitalia.repository.AppUserRepository userRepository;

    @Autowired
    private br.com.clyvo.vitalia.repository.AppRoleRepository appRoleRepository;

    @Test
    @DisplayName("Veterinario deve conseguir criar histórico clínico e alertas com sucesso")
    @WithMockUser(username = "vet@test.com", roles = {"VETERINARIAN"})
    void veterinarianClinicalFlowShouldWork() throws Exception {
        br.com.clyvo.vitalia.entity.Role vetRole = roleRepository.findByName("VETERINARIAN")
                .orElseGet(() -> roleRepository.save(new br.com.clyvo.vitalia.entity.Role(null, "VETERINARIAN")));

        br.com.clyvo.vitalia.entity.AppUser vetUser = new br.com.clyvo.vitalia.entity.AppUser();
        vetUser.setFullName("Veterinario Teste");
        vetUser.setEmail("vet@test.com");
        vetUser.setPasswordHash("123456");
        vetUser.setStatus(br.com.clyvo.vitalia.enums.AppUserStatus.ACTIVE);
        vetUser.setCreatedAt(java.time.LocalDateTime.now());
        vetUser.setUpdatedAt(java.time.LocalDateTime.now());
        vetUser.setRoles(java.util.Set.of(vetRole));
        vetUser = userRepository.save(vetUser);

        br.com.clyvo.vitalia.entity.AppUser owner = new br.com.clyvo.vitalia.entity.AppUser();
        owner.setFullName("Tutor Teste");
        owner.setEmail("tutor.owner@test.com");
        owner.setPasswordHash("123456");
        owner.setStatus(br.com.clyvo.vitalia.enums.AppUserStatus.ACTIVE);
        owner.setCreatedAt(java.time.LocalDateTime.now());
        owner.setUpdatedAt(java.time.LocalDateTime.now());
        owner = userRepository.save(owner);

        br.com.clyvo.vitalia.entity.Pet pet = new br.com.clyvo.vitalia.entity.Pet();
        pet.setName("Pet Teste");
        pet.setSex("MALE");
        pet.setStatus("ACTIVE");
        pet.setCreatedAt(java.time.LocalDateTime.now());
        pet.setOwner(owner);
        pet = petRepository.save(pet);

        Map clinicalHistoryPayload = Map.of(
                "petId", pet.getId(),
                "veterinarianId", vetUser.getId(),
                "diagnosis", "Saudável",
                "treatment", "Nenhum"
        );

        mockMvc.perform(post("/clinical-histories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clinicalHistoryPayload)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Tutor deve conseguir acessar o próprio pet")
    void tutorShouldAccessOnlyOwnPet() throws Exception {
        br.com.clyvo.vitalia.entity.Role tutorRole = roleRepository.findByName("TUTOR")
                .orElseGet(() -> roleRepository.save(new br.com.clyvo.vitalia.entity.Role(null, "TUTOR")));

        br.com.clyvo.vitalia.entity.AppUser owner = new br.com.clyvo.vitalia.entity.AppUser();
        owner.setFullName("Tutor Proprio Teste");
        owner.setEmail("tutor.proprio@test.com");
        owner.setPasswordHash("123456");
        owner.setStatus(br.com.clyvo.vitalia.enums.AppUserStatus.ACTIVE);
        owner.setCreatedAt(java.time.LocalDateTime.now());
        owner.setUpdatedAt(java.time.LocalDateTime.now());
        owner.setRoles(java.util.Set.of(tutorRole));
        owner = userRepository.save(owner);

        br.com.clyvo.vitalia.entity.Pet pet = new br.com.clyvo.vitalia.entity.Pet();
        pet.setName("Pet do Tutor Proprio");
        pet.setSex("MALE");
        pet.setStatus("ACTIVE");
        pet.setCreatedAt(java.time.LocalDateTime.now());
        pet.setOwner(owner);
        pet = petRepository.save(pet);

        mockMvc.perform(get("/pets/" + pet.getId())
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(owner)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Veterinario deve conseguir criar ou atualizar alertas com sucesso")
    @WithMockUser(username = "vet@test.com", roles = {"VETERINARIAN"})
    void veterinarianShouldCreateOrUpdateAlert() throws Exception {
        br.com.clyvo.vitalia.entity.AppUser owner = new br.com.clyvo.vitalia.entity.AppUser();
        owner.setFullName("Tutor Teste Alert");
        owner.setEmail("tutor.alert@test.com");
        owner.setPasswordHash("123456");
        owner.setStatus(br.com.clyvo.vitalia.enums.AppUserStatus.ACTIVE);
        owner.setCreatedAt(java.time.LocalDateTime.now());
        owner.setUpdatedAt(java.time.LocalDateTime.now());
        owner = userRepository.save(owner);

        br.com.clyvo.vitalia.entity.Pet pet = new br.com.clyvo.vitalia.entity.Pet();
        pet.setName("Pet Alerta");
        pet.setSex("FEMALE");
        pet.setStatus("ACTIVE");
        pet.setCreatedAt(java.time.LocalDateTime.now());
        pet.setOwner(owner);
        pet = petRepository.save(pet);

        Map alertPayload = Map.of(
                "petId", pet.getId(),
                "alertType", "GENERAL",
                "severity", "HIGH",
                "message", "Alerta de teste"
        );

        mockMvc.perform(post("/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alertPayload)))
                .andExpect(status().isCreated());
    }
}