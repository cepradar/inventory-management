package com.inventory.config;

import com.inventory.service.UsuarioService;
import com.inventory.util.JwtFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final SecurityService securityService;

    public SecurityConfig(JwtFilter jwtFilter, SecurityService securityService) {
        this.jwtFilter = jwtFilter;
        this.securityService = securityService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UsuarioService userService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(securityService.passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeRequests(auth -> auth
                        .requestMatchers("/api/public/**", "/auth/register", "/auth/login", "/api/categories/**","/products/**")
                        .permitAll() // No necesita autenticación
                        /*.requestMatchers("/products/**").hasRole("ADMIN") // Solo usuarios autenticados pueden acceder a
                                                                         // productos*/
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
