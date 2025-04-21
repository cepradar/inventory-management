package com.inventory.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "usuarios")
public class User implements UserDetails {
    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roles_name", nullable = false)
    private Rol role;

    @Lob
    @Column(nullable = true) // El campo es opcional
    private byte[] profilePicture;

    // Constructor
    public User() {
    }

    public User(String username, String password, Rol role, byte[] profilePicture) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.profilePicture = profilePicture;

    }

    
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return "Usuarios [username=" + username + ", role=" + role + ", profilePicture="
                + Arrays.toString(profilePicture) + "]";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "ROLE_" + role); // Asumiendo que los roles son almacenados
                                                                          // como "ADMIN", "USER", etc.
    }


    

    
}
