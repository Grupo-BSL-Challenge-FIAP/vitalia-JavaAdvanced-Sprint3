package br.com.clyvo.vitalia;

import br.com.clyvo.vitalia.dto.response.PetResponse;
import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.entity.Pet;
import br.com.clyvo.vitalia.entity.Role;
import br.com.clyvo.vitalia.repository.PetRepository;
import br.com.clyvo.vitalia.service.PetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import java.util.Set;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    @Test
    void deveRetornarPetQuandoIdExistir() {
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Rex");

        AppUser admin = new AppUser();
        Role roleAdmin = new Role();
        roleAdmin.setName("ADMIN");
        admin.setRoles(Set.of(roleAdmin));

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        PetResponse resultado = petService.findById(1L, admin);

        assertNotNull(resultado);
        assertEquals("Rex", resultado.name());
        verify(petRepository, times(1)).findById(1L);
    }
}