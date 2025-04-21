package com.inventory.dto;

import com.inventory.model.Cliente;

public class ClienteDto {

    private Long nit;
    private String nombre;
    private String telefono;
    private String direccion;

    // Constructor
    public ClienteDto(Long nit, String nombre, String telefono, String direccion) {
        this.nit = nit;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
    }


    
    // Getters y Setters
    public Long getNit() {
        return nit;
    }

    public void setNit(Long nit) {
        this.nit = nit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "ClienteDto [nit=" + nit + ", nombre=" + nombre + ", telefono=" + telefono + ", direccion=" + direccion + "]";
    }

    public static Cliente toCliente(Cliente clienteDto){
        Cliente cliente = new Cliente();
        cliente.setDireccion(clienteDto.getDireccion());
        cliente.setNit(clienteDto.getNit());
        cliente.setNombre(clienteDto.getNombre());
        cliente.setTelefono(clienteDto.getTelefono());

        return cliente;
    }
    

}
