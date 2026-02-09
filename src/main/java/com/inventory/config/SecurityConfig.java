package com.inventory.config;

import com.inventory.util.JwtFilter;
import com.inventory.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] allowedOriginPatterns;

    public SecurityConfig(@Value("${app.cors.allowed-origin-patterns:http://localhost:5173,https://*.trycloudflare.com}") String patterns) {
        this.allowedOriginPatterns = patterns.split("\\s*,\\s*");
    }

    // Se elimina la inyección de JwtFilter a través del constructor
    // No se necesita un campo final para JwtFilter aquí

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UsuarioService userService) {
        // Spring inyectará automáticamente UsuarioService aquí, ya que es un @Service
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder()); // Utiliza el bean PasswordEncoder definido en esta clase
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter, UsuarioService usuarioService) throws Exception { // AHORA JwtFilter es un parámetro aquí
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.setAllowedOriginPatterns(Arrays.asList(allowedOriginPatterns));
                corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                corsConfig.setAllowedHeaders(java.util.List.of("*"));
                corsConfig.setAllowCredentials(true);
                corsConfig.setMaxAge(3600L);
                return corsConfig;
            }))
            .authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    .requestMatchers("/auth/register", "/auth/login", "/api/public/**").permitAll()
    .requestMatchers("/api/company/info", "/api/company/*/logo", "/api/company/*/logo2").permitAll()
    .requestMatchers("/auth/update-profile-picture", "/auth/update-password", "/auth/logout").authenticated()

    // Requiere el rol ADMIN para configuración crítica
    .requestMatchers(HttpMethod.GET, "/api/categories/listarCategoria").hasAnyRole("ADMIN", "CLIENTE")
    .requestMatchers(HttpMethod.GET, "/api/products/listar").hasAnyRole("ADMIN", "CLIENTE")
    .requestMatchers(HttpMethod.GET, "/api/servicios/listar").authenticated() // Permitir lectura de servicios para órdenes
    .requestMatchers("/api/products/**", "/api/categories/**", "/products/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.POST, "/api/auditoria/registrar").authenticated() // Permitir registro de eventos a usuarios autenticados
    .requestMatchers("/api/auditoria/**").hasAnyRole("ADMIN", "TECNICO")
    .requestMatchers("/api/ventas/**").hasAnyRole("ADMIN", "TECNICO")
    .requestMatchers("/api/permissions/role/**").authenticated()
    .requestMatchers("/api/permissions/**").hasRole("ADMIN")
    .requestMatchers("/api/roles/**").hasRole("ADMIN")
    
    // Permite usuarios autenticados para gestión de clientes y servicios
    .requestMatchers("/api/clientes/**").authenticated()
    .requestMatchers(HttpMethod.GET, "/api/client-categories/listar").authenticated()
    .requestMatchers("/api/cliente-electrodomesticos/**").authenticated()
    .requestMatchers("/api/servicios-reparacion/**").authenticated()
    .requestMatchers("/api/marcas-electrodomestico/**").authenticated()
    .requestMatchers("/api/categorias-electrodomestico/**").authenticated()
    .requestMatchers("/api/users/technicians").authenticated() // Para asignar técnicos en órdenes

    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider(usuarioService)) // Pasamos el bean UsuarioService
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
    
}