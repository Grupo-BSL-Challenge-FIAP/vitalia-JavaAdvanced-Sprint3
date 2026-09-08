package br.com.clyvo.vitalia.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Rotas públicas de autenticação e documentação
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register/tutor").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register/vet").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/auth/register/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/error").permitAll()

                        // 1. Restrição de Usuários (/users/**) apenas para ADMIN
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // Permissões de Pets
                        .requestMatchers(HttpMethod.GET, "/pets/my-pets").hasRole("TUTOR")
                        .requestMatchers(HttpMethod.GET, "/pets/search/name").hasAnyRole("VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/pets/**").hasAnyRole("VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/pets/**").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/pets/**").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/pets/**").hasAnyRole("TUTOR", "ADMIN")

                        // 2. Revisão de Clinical Histories (POST, PUT, DELETE para VETERINARIAN ou ADMIN)
                        .requestMatchers(HttpMethod.POST, "/clinical-histories/**").hasAnyRole("VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/clinical-histories/**").hasAnyRole("VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/clinical-histories/**").hasAnyRole("VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/clinical-histories/**").hasAnyRole("TUTOR", "VETERINARIAN", "ADMIN")

                        // 3. Revisão de Alerts (Modificações restritas a VETERINARIAN ou ADMIN)
                        .requestMatchers(HttpMethod.POST, "/alerts/**").hasAnyRole("VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/alerts/**").hasAnyRole("VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/alerts/**").hasAnyRole("VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/alerts/**").hasAnyRole("TUTOR", "VETERINARIAN", "ADMIN")

                        // Permissões de Consultas (Appointments)
                        .requestMatchers(HttpMethod.GET, "/appointments/pet/**").hasAnyRole("TUTOR", "VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/appointments").hasAnyRole("VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/appointments/**").hasAnyRole("TUTOR", "VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/appointments/**").hasAnyRole("VETERINARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/appointments/**").hasAnyRole("VETERINARIAN", "ADMIN")

                        // Qualquer outra requisição precisa estar autenticada
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}