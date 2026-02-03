package com.inventory.service;

import com.inventory.dto.UpdatePswUserDto;
import com.inventory.dto.UserDto;
import com.inventory.model.Rol;
import com.inventory.model.User;
import com.inventory.repository.RolesRepository;
import com.inventory.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UsuarioService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolesRepository roleRepository;

    public User registerUser(UpdatePswUserDto actualizarUsuariosDto) {
        // Verificamos si el rol existe por nombre (usando el DTO para obtener el nombre)
        Rol role = roleRepository.findByName(actualizarUsuariosDto.getRole().getName());
        if (role == null) {
            throw new IllegalArgumentException("El rol especificado no existe");
        }

        User user = UpdatePswUserDto.toUsuarios(actualizarUsuariosDto);
        // Aquí debes cifrar la contraseña antes de guardar el usuario
        user.setPassword(passwordEncoder.encode(actualizarUsuariosDto.getNewPassword())); // Aquí se cifra la contraseña

        return userRepository.save(user);
    }

    public Optional<UserDto> findByUsername(String username) {
        // Buscamos el usuario por USERNAME
        Optional<User> usuarios = userRepository.findByUsername(username);

        // Si se encuentra, lo convertimos a UsuarioDto y lo devolvemos
        return usuarios.map(UserDto::new);
    }

    public List<UserDto> obtenerUsuarios() {
        // Obtenemos todos los categorias desde la base de datos
        List<User> usuarios = userRepository.findAll();

        // Convertimos la lista de usuarios a una lista de UsuariosDto
        return usuarios.stream()
                .map(UserDto::new) // Convierte cada usuario en UsuariosDto
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithoutPicture(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));
        return user; // User implementa UserDetails
    }

    public Boolean updateProfilePicture(String username, MultipartFile file) throws IOException {
        // Verificar que el usuario existe
        if (!userRepository.findByUsernameWithoutPicture(username).isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        // Guardar la foto SOLO en la BD sin cargar el usuario completo
        byte[] fileBytes = file.getBytes();
        userRepository.updateProfilePictureByUsername(fileBytes, username);
        return true;
    }

    public Optional<byte[]> getProfilePicture(String username) {
        return userRepository.findProfilePictureByUsername(username);
    }

    public Boolean updatePassword(UpdatePswUserDto usuarioDto) {
        // Buscar el usuario por username
        Optional<User> userOptional = userRepository.findByUsername(usuarioDto.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (isValidPassword(usuarioDto.getNewPassword())) {
                // Ciframos la nueva contraseña
                user.setPassword(passwordEncoder.encode(usuarioDto.getNewPassword()));
                // Guardamos el usuario con la nueva contraseña
                userRepository.save(user);
                new UserDto(user);
                return true; // Devolvemos el DTO del usuario actualizado
            }
            throw new IllegalArgumentException("La nueva contraseña no cumple con los requisitos de seguridad");
        }
        throw new IllegalArgumentException("Usuario no encontrado");
    }

    private boolean isValidPassword(String password) {
        // Lógica para validar la contraseña
        return password != null && password.length() >= 8; // Ejemplo de validación
    }

    public User createUser(UserDto userDto) {
        // Verificar que el usuario no exista
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        if (userDto.getRole() != null) {
            Rol role = roleRepository.findByName(userDto.getRole());
            user.setRole(role);
        }
        user.setProfilePicture(userDto.getProfilePicture());
        
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateUser(String username, UserDto userDto) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (userDto.getRole() != null) {
                Rol role = roleRepository.findByName(userDto.getRole());
                user.setRole(role);
            }
            if (userDto.getProfilePicture() != null) {
                user.setProfilePicture(userDto.getProfilePicture());
            }
            if (userDto.getFirstName() != null) {
                user.setFirstName(userDto.getFirstName());
            }
            if (userDto.getLastName() != null) {
                user.setLastName(userDto.getLastName());
            }
            if (userDto.getEmail() != null) {
                user.setEmail(userDto.getEmail());
            }
            if (userDto.getTelefono() != null) {
                user.setTelefono(userDto.getTelefono());
            }
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("Usuario no encontrado");
    }

    public Boolean deleteUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return true;
        }
        throw new IllegalArgumentException("Usuario no encontrado");
    }

    public List<UserDto> obtenerTecnicos() {
        // Obtenemos todos los usuarios y filtramos por rol TECNICO
        List<User> usuarios = userRepository.findAll();

        // Convertimos la lista de usuarios a una lista de UsuariosDto y filtramos por rol TECNICO
        return usuarios.stream()
                .filter(user -> user.getRole() != null && "TECNICO".equalsIgnoreCase(user.getRole().getName()))
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
