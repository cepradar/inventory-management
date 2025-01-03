package com.inventory.service;

import com.inventory.dto.ActualizarPSWUsuarioDto;
import com.inventory.dto.UsuariosDto;
import com.inventory.model.Roles;
import com.inventory.model.Usuarios;
import com.inventory.repository.RolesRepository;
import com.inventory.repository.UsuariosRepository;

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
    private UsuariosRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolesRepository roleRepository;

//PENDIENTE REGISTERUSER POR ERROR

    public Usuarios registerUser(ActualizarPSWUsuarioDto actualizarUsuariosDto) {
        // Verificamos si el rol existe por nombre (usando el DTO para obtener el
        // nombre)
        Roles role = roleRepository.findByName(actualizarUsuariosDto.getRole().getName());
        if (role == null) {
            throw new IllegalArgumentException("El rol especificado no existe");
        }

        Usuarios user = ActualizarPSWUsuarioDto.toUsuarios(actualizarUsuariosDto);
        // Aquí debes cifrar la contraseña antes de guardar el usuario
        user.setPassword(passwordEncoder.encode(actualizarUsuariosDto.getNewPassword())); // Aquí se cifra la contraseña

        return userRepository.save(user);
    }

    public Optional<UsuariosDto> findByUsername(String username) {
        // Buscamos el usuario por USERNAME
        Optional<Usuarios> usuarios = userRepository.findByUsername(username);

        // Si se encuentra, lo convertimos a UsuarioDto y lo devolvemos
        return usuarios.map(UsuariosDto::new);
    }

    public List<UsuariosDto> obtenerUsuarios() {
        // Obtenemos todos los categorias desde la base de datos
        List<Usuarios> usuarios = userRepository.findAll();

        // Convertimos la lista de usuarios a una lista de UsuariosDto
        return usuarios.stream()
                .map(UsuariosDto::new) // Convierte cada usuario en UsuariosDto
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuarios user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));
        return user; // User implementa UserDetails
    }

    public Boolean updateProfilePicture(String username, MultipartFile file) throws IOException {
        Optional<Usuarios> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            Usuarios user = userOptional.get();
            // Lógica para guardar la imagen de perfil, por ejemplo, en BD
            byte[] filePath = file.getBytes();
            user.setProfilePicture(filePath);
            userRepository.save(user);
            new UsuariosDto(user);
            return true;
        }
        throw new IllegalArgumentException("Usuario no encontrado");
    }

    //este metodo se usaba en updateProfilePicture pero decidi reemplazarlo por el uso de file.getBytes() en dicha funcion,
    //no se usa en otra parte del codigo.
/*
    private byte[] saveProfilePicture(MultipartFile file) throws IOException {
        // Convertimos el archivo MultipartFile a un arreglo de bytes (byte[])
        return file.getBytes();
    }
         */

    public Boolean updatePassword(ActualizarPSWUsuarioDto usuarioDto) {
        // Buscar el usuario por username
        Optional<Usuarios> userOptional = userRepository.findByUsername(usuarioDto.getUsername());
        if (userOptional.isPresent()) {
            Usuarios user = userOptional.get();
            if (isValidPassword(usuarioDto.getNewPassword())) {
                // Ciframos la nueva contraseña
                user.setPassword(passwordEncoder.encode(usuarioDto.getNewPassword()));
                // Guardamos el usuario con la nueva contraseña
                userRepository.save(user);
                new UsuariosDto(user);
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
}
