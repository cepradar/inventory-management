package com.inventory.controller;

import com.inventory.dto.ClientRegisterRequest;
import com.inventory.dto.UpdatePswUserDto;
import com.inventory.dto.UserDto;
import com.inventory.dto.LoginRequest;
import com.inventory.dto.RegisterRequest;
import com.inventory.model.Rol;
import com.inventory.model.User;
import com.inventory.service.UsuarioService;
import com.inventory.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
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
    public User registerUser(@RequestBody RegisterRequest registerRequest) {
        //Convertir la solicitud a Dto
        UpdatePswUserDto actualizarPSWUsuarioDto = new UpdatePswUserDto(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getRole());
        // Registrar un nuevo usuario con nombre de usuario, contraseña y rol
        return userService.registerUser(actualizarPSWUsuarioDto);
    }

    /**
     * Endpoint público para registrar clientes desde el landing page
     * El email se usa como username y automáticamente se asigna el rol CLIENTE
     */
    @PostMapping("/register-client")
    public ResponseEntity<?> registerClient(@RequestBody ClientRegisterRequest request) {
        try {
            // Validaciones manuales
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo electrónico es obligatorio"));
            }
            if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo electrónico no es válido"));
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("error", "La contraseña debe tener al menos 6 caracteres"));
            }
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
            }
            if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El apellido es obligatorio"));
            }

            User newClient = userService.registerClient(
                request.getEmail().trim().toLowerCase(), // Normalizar email
                request.getPassword(),
                request.getFirstName().trim(),
                request.getLastName().trim(),
                request.getTelefono()
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "Cliente registrado exitosamente");
            response.put("username", newClient.getUsername());
            response.put("email", newClient.getEmail());
            response.put("fullName", newClient.getFirstName() + " " + newClient.getLastName());

            logger.info("Nuevo cliente registrado: {}", newClient.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al registrar cliente: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            logger.error("Error inesperado al registrar cliente: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al procesar el registro. Intente nuevamente.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest loginRequest) throws Exception {
        // Log para imprimir el cuerpo de la solicitud de login
        logger.info("Cuerpo de la solicitud de login: username={}, password={}",
                loginRequest.getUsername(), loginRequest.getPassword());

        Optional<UserDto> user = null;
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
        String role = user.get().getRole(); // UserDto.getRole() ya devuelve un String

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
        Rol rol = new Rol(jwtUtil.extractRoles(request.get("token")));

        UpdatePswUserDto actualizarPSWUsuarioDto = new UpdatePswUserDto(username, newPassword, rol);

        boolean isUpdated = userService.updatePassword(actualizarPSWUsuarioDto);

        Map<String, String> response = new HashMap<>();
        response.put("message",
                isUpdated ? "Contraseña actualizada correctamente" : "Error al actualizar la contraseña");
        return response;
    }

    @PostMapping("/update-profile-picture")
    public Map<String, String> updateProfilePicture(@RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws Exception {
        // Extraer el token del header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new Exception("Token no encontrado o formato inválido");
        }
        
        String token = authHeader.substring(7); // Elimina "Bearer "
        String username = jwtUtil.extractUsername(token);
        boolean isUpdated = userService.updateProfilePicture(username, file);

        Map<String, String> response = new HashMap<>();
        response.put("message",
                isUpdated ? "Foto de perfil actualizada correctamente" : "Error al actualizar la foto de perfil");
        return response;
    }

    @GetMapping("/profile-picture/{username}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String username) {
        var picture = userService.getProfilePicture(username);
        
        if (picture.isPresent() && picture.get().length > 0) {
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .body(picture.get());
        }
        
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/logout")
    public Map<String, String> logout() {
        // Lógica de cierre de sesión si es necesaria (como invalidar el token)
        Map<String, String> response = new HashMap<>();
        response.put("message", "Sesión cerrada correctamente");
        return response;
    }

}