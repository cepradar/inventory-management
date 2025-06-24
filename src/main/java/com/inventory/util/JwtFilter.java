package com.inventory.util;

import com.inventory.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger; // Importar la clase Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory

@Component
public class JwtFilter extends OncePerRequestFilter {

    // Declaración del logger
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService userService;

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request, @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Log para ver si el filtro se está ejecutando y qué URI se está solicitando
        logger.info("Processing request for URI: {}", request.getRequestURI());

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.info("JWT found. Extracted username: {}", username);
            } catch (Exception e) {
                // Loguear el error si no se puede extraer el username (ej. token corrupto/inválido)
                logger.error("Error al extraer el username del token: {}", e.getMessage());
                // Asegurarse de que el username sea null si hay un error
                username = null;
            }
        } else {
            logger.info("No Authorization header or not a Bearer token for URI: {}", request.getRequestURI());
        }

        // Solo procede si hay un username válido y el contexto de seguridad no está ya autenticado
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userService.loadUserByUsername(username);
                logger.info("User details loaded for: {}", username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("User {} authenticated successfully. Roles: {}", username, userDetails.getAuthorities());
                } else {
                    // Loguear si la validación del token falla
                    logger.warn("JWT validation failed for user: {}", username);
                }
            } catch (Exception e) {
                // Loguear cualquier otra excepción durante el proceso de autenticación (ej. usuario no encontrado)
                logger.error("Error during authentication process for user {}: {}", username, e.getMessage());
            }
        } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.info("User already authenticated in SecurityContext for URI: {}", request.getRequestURI());
        }


        // Continúa con el siguiente filtro en la cadena
        chain.doFilter(request, response);
    }
}