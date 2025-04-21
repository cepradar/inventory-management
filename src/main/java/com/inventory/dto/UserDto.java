package com.inventory.dto;

import com.inventory.model.Rol;
import com.inventory.model.User;

import ch.qos.logback.core.subst.Token;

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

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    

}
