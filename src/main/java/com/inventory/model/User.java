package com.inventory.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        // Asegúrate de que el rol no sea nulo antes de acceder a su nombre
        if (this.role != null && this.role.getName() != null) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.getName()));
        }
        // Si el usuario no tiene rol o el nombre del rol es nulo, devuelve una lista vacía de autoridades.
        // Considera si un usuario debería siempre tener un rol para tu aplicación.
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implementa tu lógica de expiración de cuenta si es necesario
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implementa tu lógica de bloqueo de cuenta si es necesario
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implementa tu lógica de expiración de credenciales si es necesario
    }

    @Override
    public boolean isEnabled() {
        return true; // Implementa tu lógica de habilitación de usuario si es necesario
    }
    
}
