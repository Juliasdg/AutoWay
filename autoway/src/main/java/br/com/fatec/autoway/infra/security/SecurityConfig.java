package br.com.fatec.autoway.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/pessoas/confirm").permitAll()
                        .requestMatchers(HttpMethod.POST, "/passagens/**").permitAll()

                        // libera o Swagger
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // libera também se você tiver actuator/health
                        .requestMatchers("/actuator/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/passagens/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/passagens/me").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/veiculos").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/veiculos/me").hasAnyRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/veiculos").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/veiculos/*").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/veiculos/*/ativar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/veiculos/*/inativar").hasAnyRole("CLIENTE","ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/veiculos/*/reativar").hasAnyRole("CLIENTE","ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/pessoas/me/password").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/pessoas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/pessoas/me").hasAnyRole("ADMIN","CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/pessoas/{id}").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/pessoas").hasRole("ADMIN") // Criar novo usuário
                        .requestMatchers(HttpMethod.PUT, "/pessoas/me").hasAnyRole("ADMIN","CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/pessoas/{id}").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/pessoas/{id}/inactivate").hasAnyRole("ADMIN", "CLIENTE") // Inativar
                        .requestMatchers(HttpMethod.PATCH, "/pessoas/{id}/reactivate").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
