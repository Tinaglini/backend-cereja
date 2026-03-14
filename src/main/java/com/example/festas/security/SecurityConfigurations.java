package com.example.festas.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Permitir OPTIONS (CORS preflight)
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // Endpoints públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // CLIENTES - Apenas ADMIN (endpoints sensíveis)
                        .requestMatchers("/api/clientes/**").hasRole("ADMIN")

                        // ENDEREÇOS - Apenas ADMIN (conforme solicitado)
                        .requestMatchers("/api/enderecos/**").hasRole("ADMIN")

                        // TEMAS - GET público (sem autenticação), Gestão apenas ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/temas/**")
                        .permitAll() // Qualquer pessoa pode ver temas
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/temas/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/temas/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/temas/**").hasRole("ADMIN")

                        // TIPOS DE EVENTO - GET público (sem autenticação), Gestão apenas ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/tipos-evento/**")
                        .permitAll() // Qualquer pessoa pode ver tipos de evento
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/tipos-evento/**")
                        .hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/tipos-evento/**")
                        .hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/tipos-evento/**")
                        .hasRole("ADMIN")

                        // GERENCIAMENTO DE USUÁRIOS - Apenas ADMIN
                        .requestMatchers("/api/admin/usuarios/**").hasRole("ADMIN")

                        // SOLICITAÇÕES - Lógica complexa no controller (USER+ADMIN com validação de
                        // propriedade)
                        .requestMatchers("/api/solicitacoes/**").hasAnyRole("USER", "ADMIN")

                        // Qualquer outra requisição requer autenticação
                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*", // Qualquer porta localhost (desenvolvimento)
                "http://18.231.120.168", // IP da AWS atual
                "http://18.231.120.168:*", // AWS com qualquer porta
                "http://54.232.5.122", // IP antigo
                "http://54.232.5.122:*"));
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}