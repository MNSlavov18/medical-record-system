package com.inf.medical_record_system.config;

import com.inf.medical_record_system.service.impl.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Forbidden\"}");
                        })
                )

                .authorizeHttpRequests(auth -> auth

                        // Swagger/OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Static resources for later UI
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // Users - only ADMIN
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // Specialties
                        .requestMatchers(HttpMethod.GET, "/api/specialties/**")
                        .hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                        .requestMatchers(HttpMethod.POST, "/api/specialties/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/specialties/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/specialties/**")
                        .hasRole("ADMIN")

                        // Diagnoses
                        .requestMatchers(HttpMethod.GET, "/api/diagnoses/**")
                        .hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/diagnoses/**")
                        .hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/diagnoses/**")
                        .hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/diagnoses/**")
                        .hasRole("ADMIN")

                        // Doctors
                        .requestMatchers(HttpMethod.GET, "/api/doctors/**")
                        .hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                        .requestMatchers(HttpMethod.POST, "/api/doctors/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/doctors/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/doctors/**")
                        .hasRole("ADMIN")

                        // Patients
                        .requestMatchers(HttpMethod.GET, "/api/patients/**")
                        .hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                        .requestMatchers(HttpMethod.POST, "/api/patients/**")
                        .hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/patients/**")
                        .hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/patients/**")
                        .hasRole("ADMIN")

                        // Health insurance statuses
                        .requestMatchers(HttpMethod.GET, "/api/health-insurance-statuses/**")
                        .hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                        .requestMatchers(HttpMethod.POST, "/api/health-insurance-statuses/**")
                        .hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/health-insurance-statuses/**")
                        .hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/health-insurance-statuses/**")
                        .hasRole("ADMIN")

                        // Examinations
                        .requestMatchers(HttpMethod.GET, "/api/examinations/**")
                        .hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                        .requestMatchers(HttpMethod.POST, "/api/examinations/**")
                        .hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/examinations/**")
                        .hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/examinations/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        // Treatments
                        .requestMatchers(HttpMethod.GET, "/api/treatments/**")
                        .hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                        .requestMatchers(HttpMethod.POST, "/api/treatments/**")
                        .hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/treatments/**")
                        .hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/treatments/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                                // Statistics - admin-only financial/system reports
                                .requestMatchers(HttpMethod.GET, "/api/statistics/examinations/patient-paid-total")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/statistics/examinations/patient-paid-by-doctor/**")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/statistics/personal-doctors/patient-count")
                                .hasRole("ADMIN")

// Statistics - medical reports available to ADMIN and DOCTOR
                                .requestMatchers(HttpMethod.GET, "/api/statistics/**")
                                .hasAnyRole("ADMIN", "DOCTOR")

                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())

                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Unauthorized\"}");
                        })
                )

                .formLogin(formLogin -> formLogin.disable());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}