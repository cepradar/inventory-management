package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registro de clientes desde el landing page
 * El email se usará como username del usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegisterRequest {
    
    private String email; // Requerido - se usará como username
    private String password; // Requerido - mínimo 6 caracteres
    private String firstName; // Requerido
    private String lastName; // Requerido
    private String telefono; // Opcional
}
