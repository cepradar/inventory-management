package com.inventory.dto;

import com.inventory.model.Rol;

import lombok.Data;
import lombok.NoArgsConstructor;


//genera getters and setter
@Data
//genera constructor sin argumentos
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private Rol role;

}
