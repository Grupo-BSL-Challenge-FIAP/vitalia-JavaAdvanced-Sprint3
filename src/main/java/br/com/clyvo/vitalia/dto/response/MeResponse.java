package br.com.clyvo.vitalia.dto.response;

import br.com.clyvo.vitalia.entity.Role;

import java.time.LocalDate;
import java.util.Set;

public record MeResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        String cpf,
        LocalDate dateOfBirth,
        String address,
        Set<Role> roles
) {}