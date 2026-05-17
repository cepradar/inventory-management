package com.inventory.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

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

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String telefono;

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roles_name", nullable = false)
    private Rol role;

    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = true) // El campo es opcional
    private byte[] profilePicture;

    // Constructor
    public User() {
    }

    public User(String username, String password, Rol role) {
        this.username = username;
        this.password = password;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Usuarios [username=" + username + ", role=" + role + "]";
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
