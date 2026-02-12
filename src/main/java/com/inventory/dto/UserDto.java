package com.inventory.dto;

import com.inventory.model.User;

import ch.qos.logback.core.subst.Token;
import lombok.Data;
import lombok.NoArgsConstructor;

//genera getters and setter
@Data
//genera constructor sin argumentos
@NoArgsConstructor
public class UserDto {

    private String id;
    private String username;
    private String email;
    private String telefono;
    private String firstName;
    private String lastName;
    private String role;
    private String roleColor;
    private byte[] profilePicture;
    private Token token;

    // Constructor
    public UserDto(User usuario) {
        this.id = usuario.getUsername(); // Usar username como id
        this.username = usuario.getUsername();
        this.role = usuario.getRole() != null ? usuario.getRole().getName() : "USER";
        this.roleColor = usuario.getRole() != null && usuario.getRole().getColor() != null ? usuario.getRole().getColor() : "#2563eb";
        this.profilePicture = usuario.getProfilePicture();
        this.firstName = usuario.getFirstName();
        this.lastName = usuario.getLastName();
        this.email = usuario.getEmail();
        this.telefono = usuario.getTelefono();
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
        usuarios.setFirstName(usuariosDto.getFirstName());
        usuarios.setLastName(usuariosDto.getLastName());
        usuarios.setEmail(usuariosDto.getEmail());
        usuarios.setTelefono(usuariosDto.getTelefono());

        return usuarios;
    }

}
