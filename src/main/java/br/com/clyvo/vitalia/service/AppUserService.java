package br.com.clyvo.vitalia.service;

import br.com.clyvo.vitalia.dto.request.AppUserRequest;
import br.com.clyvo.vitalia.dto.request.UpdateMeRequest;
import br.com.clyvo.vitalia.dto.response.AppUserResponse;
import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import br.com.clyvo.vitalia.enums.AppUserStatus;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository repository;

    public Page<AppUserResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::toResponse);
    }

    public AppUserResponse findById(Long id) {
        return toResponse(findUserById(id));
    }

    public AppUserResponse update(
            Long id,
            AppUserRequest request
    ) {
        AppUser user = findUserById(id);

        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setUpdatedAt(LocalDateTime.now());

        return toResponse(repository.save(user));
    }

    @Transactional
    public void updateMe(
            String email,
            UpdateMeRequest request
    ) {
        AppUser user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuário não encontrado"
                        )
                );

        user.setFullName(request.fullName());

        // Na entidade o atributo é "phone"
        user.setPhone(request.phoneNumber());

        user.setCpf(request.cpf());

        user.setDateOfBirth(
                request.dateOfBirth()
        );

        user.setAddress(
                request.address()
        );

        user.setUpdatedAt(
                LocalDateTime.now()
        );

        repository.save(user);
    }

    public void delete(Long id) {
        repository.delete(
                findUserById(id)
        );
    }

    @Transactional
    public void deleteMe(String email) {
        AppUser user = repository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuário não encontrado"
                        )
                );

        user.setStatus(AppUserStatus.BLOCKED);
        user.setUpdatedAt(LocalDateTime.now());

        repository.save(user);
    }

    private AppUser findUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuário não encontrado"
                        )
                );
    }

    private AppUserResponse toResponse(
            AppUser user
    ) {
        return new AppUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }
}