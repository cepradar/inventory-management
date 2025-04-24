package com.inventory.dto;

import com.inventory.model.Cliente;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClienteDto {

    private String id;
    private String nit;
    private String categoryId;
    private String tipoDocumentoId;
    private String nombre;
    private String telefono;
    private String direccion;
    private Boolean activo;

    // Constructor desde entidad Cliente
    public ClienteDto(Cliente cliente) {
        this.id = cliente.getId();
        this.nit = cliente.getNit();
        this.categoryId = cliente.getCategory().getId();
        this.tipoDocumentoId = cliente.getTipoId().getId();
        this.nombre = cliente.getNombre();
        this.telefono = cliente.getTelefono();
        this.direccion = cliente.getDireccion();
        this.activo = cliente.getActivo();
    }

    // Método utilitario para convertir DTO a entidad
    public static Cliente toCliente(ClienteDto clienteDto) {
        Cliente cliente = new Cliente();
        cliente.setDireccion(clienteDto.getDireccion());
        cliente.setNit(clienteDto.getNit());
        cliente.setNombre(clienteDto.getNombre());
        cliente.setTelefono(clienteDto.getTelefono());
        cliente.setActivo(clienteDto.getActivo());
        return cliente;
    }
}
