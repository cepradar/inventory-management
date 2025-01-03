package com.inventory.dto;

import com.inventory.model.Roles;
import com.inventory.model.Usuarios;

import ch.qos.logback.core.subst.Token;

public class UsuariosDto {

    private String username;
    private Roles role;
    private byte[] profilePicture;
    private Token token;

    // Constructor
    public UsuariosDto(Usuarios usuario) {
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

    // Método toString (opcional)
    @Override
    public String toString() {
        return "UsuarioDto{username=" + username + "', roleName='" + role + "', profilePicture='" + profilePicture
                + "'}";
    }

    public static Usuarios toUsuarios(UsuariosDto usuariosDto) {
        Usuarios usuarios = new Usuarios();
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
