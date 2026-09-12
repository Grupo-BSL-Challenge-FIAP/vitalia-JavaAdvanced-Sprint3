package br.com.clyvo.vitalia.controller;

import br.com.clyvo.vitalia.dto.request.LoginRequest;
import br.com.clyvo.vitalia.dto.request.RegisterRequest;
import br.com.clyvo.vitalia.dto.response.LoginResponse;
import br.com.clyvo.vitalia.dto.response.MeResponse;
import br.com.clyvo.vitalia.dto.request.MobileRegisterRequest;
import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.enums.AppUserStatus;
import br.com.clyvo.vitalia.repository.AppRoleRepository;
import br.com.clyvo.vitalia.repository.AppUserRepository;
import br.com.clyvo.vitalia.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import br.com.clyvo.vitalia.entity.Role;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var user = (AppUser) auth.getPrincipal();
        var token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token, user.getId(), user.getRoles()));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> getCurrentUser(@AuthenticationPrincipal AppUser user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        MeResponse response = new MeResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getCpf(),
                user.getDateOfBirth(),
                user.getAddress(),
                user.getRoles()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/tutor")
    @Transactional
    public ResponseEntity<Void> registerTutor(@RequestBody @Valid RegisterRequest data) {
        return registerUserWithRole(data, "TUTOR");
    }

    @PostMapping("/register/vet")
    @Transactional
    public ResponseEntity<Void> registerVet(@RequestBody @Valid RegisterRequest data) {
        return registerUserWithRole(data, "VETERINARIAN");
    }

    @PostMapping("/register/admin")
    @Transactional
    public ResponseEntity<Void> registerAdmin(@RequestBody @Valid RegisterRequest data) {
        return registerUserWithRole(data, "ADMIN");
    }

    private ResponseEntity registerUserWithRole(RegisterRequest data, String roleName) {
        if (this.appUserRepository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        var role = this.appRoleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " não encontrada"));

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        LocalDateTime now = LocalDateTime.now();

        AppUser newUser = AppUser.builder()
                .fullName(data.fullName())
                .email(data.email())
                .passwordHash(encryptedPassword)
                .phone(data.phoneNumber())
                .cpf(data.cpf())
                .dateOfBirth(data.dateOfBirth())
                .address(data.address())
                .status(AppUserStatus.ACTIVE)
                .roles(new HashSet<>(Set.of(role)))
                .createdAt(now)
                .updatedAt(now)
                .build();

        this.appUserRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Void> register(@RequestBody @Valid MobileRegisterRequest data) {
        if (this.appUserRepository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        var tutorRole = this.appRoleRepository.findByName("TUTOR")
                .orElseThrow(() -> new RuntimeException("Role TUTOR não encontrada"));

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        LocalDateTime now = LocalDateTime.now();
        AppUser newUser = AppUser.builder()
                .fullName(data.fullName())
                .email(data.email())
                .passwordHash(encryptedPassword)
                .phone(data.phoneNumber())
                .status(AppUserStatus.ACTIVE)
                .roles(new HashSet<Role>(Collections.singleton(tutorRole)))
                .createdAt(now)
                .updatedAt(now)
                .build();

        this.appUserRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}