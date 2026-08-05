package com.wdenberg.task.infra.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfigurations {


    private final SecurityFilter securityFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http

                // API usando JWT não precisa de CSRF
                .csrf(AbstractHttpConfigurer::disable)


                // Não cria sessão no servidor
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // Tratamento correto de autenticação
                .exceptionHandling(exception -> exception

                        // Token ausente, expirado ou inválido
                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter()
                                            .write("""
                                            {
                                                "status": 401,
                                                "message": "Token inválido ou expirado"
                                            }
                                            """);
                                }
                        )

                        // Usuário autenticado mas sem permissão
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter()
                                            .write("""
                                            {
                                                "status": 403,
                                                "message": "Acesso negado"
                                            }
                                            """);
                                }
                        )
                )


                .authorizeHttpRequests(auth -> auth


                        // Login público
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/login"
                        ).permitAll()


                        // Cadastro público
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/register"
                        ).permitAll()


                        // Swagger público
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()


                        // Qualquer outro endpoint exige JWT
                        .anyRequest().authenticated()
                )


                // Nosso filtro JWT roda antes do filtro padrão
                .addFilterBefore(
                        securityFilter,
                        UsernamePasswordAuthenticationFilter.class
                )


                .build();
    }



    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }



    @Bean
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }
}