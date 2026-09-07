package br.com.clyvo.vitalia;

import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.entity.Role;
import br.com.clyvo.vitalia.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "minha-chave-secreta-de-teste");
    }

    @Test
    void deveGerarEValidarTokenComSucesso() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setEmail("vet@clyvo.com");

        Role role = new Role();
        role.setName("VETERINARIAN");
        user.setRoles(Set.of(role));

        String token = tokenService.generateToken(user);
        assertNotNull(token);

        String subject = tokenService.validateToken(token);
        assertEquals("vet@clyvo.com", subject);
    }
}