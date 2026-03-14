package com.casting.platform.config;

import com.casting.platform.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())

                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable())
                .logout(l -> l.disable())

                // 🔥 JSON ошибки вместо HTML
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "Unauthorized"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                                        "Forbidden"))
                )

                .authorizeHttpRequests(auth -> auth

                        // ===== PUBLIC =====
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/uploads/**",
                                "/mock-pay/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/payments/webhook").permitAll()

                        // ===== FILE UPLOAD =====
                        .requestMatchers("/api/files/**").authenticated()

                        // ===== CATALOG PUBLIC =====
                        .requestMatchers("/api/catalog/**").permitAll()

                        // ===== CUSTOMER =====
                        .requestMatchers("/api/customer/**")
                        .hasRole("CUSTOMER")

                        // ===== PERFORMER =====
                        .requestMatchers("/api/profile/**")
                        .hasAnyRole("CREATOR", "ACTOR", "LOCATION_OWNER", "ADMIN")


                        // ===== SUBSCRIPTIONS =====
                        .requestMatchers("/api/subscriptions/**")
                        .hasRole("CUSTOMER")
                        .requestMatchers("/api/payments/**")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        // ===== ADMIN =====
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // ===== EVERYTHING ELSE =====
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    private void writeJson(HttpServletResponse response, int status, String message)
            throws java.io.IOException {

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String body = """
                {
                  "message": "%s",
                  "status": %d
                }
                """.formatted(message, status);

        response.getWriter().write(body);
    }
}
