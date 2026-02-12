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
public class UpdatePswUserDto {
    
    private String username;
    private String newPassword;    
    private Rol role;
    private byte[] profilePicture;
    private Token token;

    // Constructor

    public UpdatePswUserDto(User usuarios) {
        this.username = usuarios.getUsername();
        this.newPassword = usuarios.getPassword();
        this.role = usuarios.getRole();
        this.profilePicture = usuarios.getProfilePicture();
    }


    public UpdatePswUserDto(String username, String newPassword, Rol rol) {
        this.username = username;
        this.newPassword = newPassword;
        this.role = rol;
    }

    public static User toUsuarios(UpdatePswUserDto usuariosDto) {
        User usuarios = new User();
        usuarios.setUsername(usuariosDto.getUsername());
        usuarios.setPassword(usuariosDto.getNewPassword());
        usuarios.setRole(usuariosDto.getRole());
        usuarios.setProfilePicture(usuariosDto.getProfilePicture());

        return usuarios;
    }

    
    
}
