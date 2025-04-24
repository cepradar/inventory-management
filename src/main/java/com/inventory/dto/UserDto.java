package com.inventory.dto;

import com.inventory.model.Rol;
import com.inventory.model.User;

import ch.qos.logback.core.subst.Token;
import lombok.Data;
import lombok.NoArgsConstructor;

//genera getters and setter
@Data
//genera constructor sin argumentos
@NoArgsConstructor
public class UserDto {

    private String username;
    private Rol role;
    private byte[] profilePicture;
    private Token token;

    // Constructor
    public UserDto(User usuario) {
        this.username = usuario.getUsername();
        this.role = usuario.getRole(); // Asumiendo que el Usuario tiene un Rol
        this.profilePicture = usuario.getProfilePicture();
    }


    // Método toString (opcional)
    @Override
    public String toString() {
        return "UsuarioDto{username=" + username + "', roleName='" + role + "', profilePicture='" + profilePicture
                + "'}";
    }

    public static User toUsuarios(UserDto usuariosDto) {
        User usuarios = new User();
        usuarios.setUsername(usuariosDto.getUsername());
        usuarios.setProfilePicture(usuariosDto.getProfilePicture());
        usuarios.setRole(usuariosDto.getRole());

        return usuarios;
    }

}
