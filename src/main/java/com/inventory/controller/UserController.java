package com.inventory.controller;

import com.inventory.dto.ActualizarPSWUsuarioDto;
import com.inventory.dto.LoginRequest;
import com.inventory.dto.RegisterRequest;
import com.inventory.dto.UsuariosDto;
import com.inventory.model.Roles;
import com.inventory.model.Usuarios;
import com.inventory.service.UsuarioService;
import com.inventory.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UsuarioService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Usuarios registerUser(@RequestBody RegisterRequest registerRequest) {
        //Convertir la solicitud a Dto
        ActualizarPSWUsuarioDto actualizarPSWUsuarioDto = new ActualizarPSWUsuarioDto(registerRequest.getUsername(), registerRequest.getPassword(), null, registerRequest.getRole());
        // Registrar un nuevo usuario con nombre de usuario, contraseña y rol
        return userService.registerUser(actualizarPSWUsuarioDto);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest loginRequest) throws Exception {
        // Log para imprimir el cuerpo de la solicitud de login
        logger.info("Cuerpo de la solicitud de login: username={}, password={}",
                loginRequest.getUsername(), loginRequest.getPassword());

        Optional<UsuariosDto> user = null;
        try {
            // Autenticación del usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Recuperar el usuario autenticado de la base de datos
            user = userService.findByUsername(loginRequest.getUsername());
            if (user == null) {
                throw new Exception("Usuario no encontrado");
            }

        } catch (AuthenticationException e) {
            throw new Exception("Credenciales inválidas: " + e.getMessage());
        }

        // Obtener el rol del usuario autenticado
        String role = user.get().getRole().getName(); // Obtener el rol del usuario desde la base de datos

        // Generar token JWT para el usuario autenticado
        String token = jwtUtil.generateToken(loginRequest.getUsername(), role);

        // Log del token generado
        logger.info("Token generado para el usuario {}: {}", loginRequest.getUsername(), token);

        // Devolver el token y la información del usuario en un objeto JSON
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", loginRequest.getUsername());
        response.put("role", role); // Aquí devolvemos solo el nombre del rol, no el objeto Role
        response.put("message", "Inicio de sesión exitoso");

        return response;
    }

    @PostMapping("/validate")
    public String validateToken(@RequestBody String token) {
        // Validación del token para asegurar que no ha expirado y que el rol es
        // adecuado
        if (jwtUtil.isTokenExpired(token)) {
            return "Token expirado";
        }

        // Aquí podrías hacer una validación adicional de roles
        if (!jwtUtil.hasRole(token, "ADMIN")) {
            return "Acceso denegado: rol no autorizado";
        }

        return "Token válido";
    }

    @PostMapping("/change-password")
    public Map<String, String> changePassword(@RequestBody Map<String, String> request) {
        String username = jwtUtil.extractUsername(request.get("token"));
        String newPassword = request.get("newPassword");

        ActualizarPSWUsuarioDto actualizarPSWUsuarioDto = new ActualizarPSWUsuarioDto(username, newPassword, null, newPassword);

        boolean isUpdated = userService.updatePassword(actualizarPSWUsuarioDto);

        Map<String, String> response = new HashMap<>();
        response.put("message",
                isUpdated ? "Contraseña actualizada correctamente" : "Error al actualizar la contraseña");
        return response;
    }

    @PostMapping("/update-profile-picture")
    public Map<String, String> updateProfilePicture(@RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) throws Exception {
        String username = jwtUtil.extractUsername(token.substring(7)); // Elimina "Bearer "
        boolean isUpdated = userService.updateProfilePicture(username, file);

        Map<String, String> response = new HashMap<>();
        response.put("message",
                isUpdated ? "Foto de perfil actualizada correctamente" : "Error al actualizar la foto de perfil");
        return response;
    }

    @PostMapping("/logout")
    public Map<String, String> logout() {
        // Lógica de cierre de sesión si es necesaria (como invalidar el token)
        Map<String, String> response = new HashMap<>();
        response.put("message", "Sesión cerrada correctamente");
        return response;
    }

}
