package com.inventory.controller;

import com.inventory.dto.UserDto;
import com.inventory.model.User;
import com.inventory.model.Rol;
import com.inventory.service.UsuarioService;
import com.inventory.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private UsuarioService userService;

    @Autowired
    private RolesRepository roleRepository;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> users = userService.obtenerUsuarios();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error al obtener usuarios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/roles/available")
    public ResponseEntity<List<Rol>> getAvailableRoles() {
        try {
            List<Rol> roles = roleRepository.findAll();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            logger.error("Error al obtener roles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/technicians")
    public ResponseEntity<List<UserDto>> getTechnicians() {
        try {
            List<UserDto> technicians = userService.obtenerTecnicos();
            return ResponseEntity.ok(technicians);
        } catch (Exception e) {
            logger.error("Error al obtener técnicos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        try {
            Optional<UserDto> userDto = userService.findByUsername(username);
            if (userDto.isPresent()) {
                return ResponseEntity.ok(userDto.get());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error al obtener usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        try {
            // Buscar el rol en la base de datos
            Rol role = roleRepository.findByName(userDto.getRole());
            if (role == null) {
                logger.error("El rol especificado no existe: {}", userDto.getRole());
                return ResponseEntity.badRequest().body(null);
            }
            
            // Crear el DTO para registrar el usuario
            com.inventory.dto.UpdatePswUserDto updatePswUserDto = new com.inventory.dto.UpdatePswUserDto(
                userDto.getUsername(),
                "TempPassword123!",
                role
            );
            
            User createdUser = userService.registerUser(updatePswUserDto);
            
            // Actualizar campos adicionales
            if (userDto.getEmail() != null) createdUser.setEmail(userDto.getEmail());
            if (userDto.getFirstName() != null) createdUser.setFirstName(userDto.getFirstName());
            if (userDto.getLastName() != null) createdUser.setLastName(userDto.getLastName());
            
            userService.saveUser(createdUser);
            
            logger.info("Usuario creado exitosamente: {}", userDto.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(new UserDto(createdUser));
        } catch (Exception e) {
            logger.error("Error al crear usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String username, @RequestBody UserDto userDto) {
        try {
            User updatedUser = userService.updateUser(username, userDto);
            logger.info("Usuario actualizado exitosamente: {}", username);
            return ResponseEntity.ok(new UserDto(updatedUser));
        } catch (IllegalArgumentException e) {
            logger.error("Usuario no encontrado: {}", username);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error al actualizar usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        try {
            userService.deleteUser(username);
            logger.info("Usuario eliminado exitosamente: {}", username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Usuario no encontrado: {}", username);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error al eliminar usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
