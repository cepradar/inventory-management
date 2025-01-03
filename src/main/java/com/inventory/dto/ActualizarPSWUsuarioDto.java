package com.inventory.dto;

import com.inventory.model.Roles;
import com.inventory.model.Usuarios;

import ch.qos.logback.core.subst.Token;

public class ActualizarPSWUsuarioDto {
    
    private String username;
    private String newPassword;    
    private Roles role;
    private byte[] profilePicture;
    private Token token;

    // Constructor
    

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public ActualizarPSWUsuarioDto(Usuarios usuarios) {
        this.username = usuarios.getUsername();
        this.newPassword = usuarios.getPassword();
        this.role = usuarios.getRole();
        this.profilePicture = usuarios.getProfilePicture();
    }

    public ActualizarPSWUsuarioDto() {
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public ActualizarPSWUsuarioDto(String username, String newPassword, byte[] profilePicture, String role) {
        this.username = username;
        this.newPassword = newPassword;
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

    public static Usuarios toUsuarios(ActualizarPSWUsuarioDto usuariosDto) {
        Usuarios usuarios = new Usuarios();
        usuarios.setUsername(usuariosDto.getUsername());
        usuarios.setPassword(usuariosDto.getNewPassword());
        usuarios.setRole(usuarios.getRole());
        usuarios.setProfilePicture(usuarios.getProfilePicture());

        return usuarios;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
    
}
