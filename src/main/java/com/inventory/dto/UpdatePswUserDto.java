package com.inventory.dto;

import com.inventory.model.Rol;
import com.inventory.model.User;

import ch.qos.logback.core.subst.Token;

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

    public UpdatePswUserDto() {
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public Rol getRole() {
        return role;
    }

    public void setRole(Rol role) {
        this.role = role;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public static User toUsuarios(UpdatePswUserDto usuariosDto) {
        User usuarios = new User();
        usuarios.setUsername(usuariosDto.getUsername());
        usuarios.setPassword(usuariosDto.getNewPassword());
        usuarios.setRole(usuariosDto.getRole());
        usuarios.setProfilePicture(usuariosDto.getProfilePicture());

        return usuarios;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
    
}
