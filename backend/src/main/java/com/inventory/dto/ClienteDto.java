package com.inventory.dto;

import com.inventory.model.Cliente;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClienteDto {

    private String documento;  // ID del cliente (documento)
    private String nit;
    private String categoryId;
    private String categoryName;
    private String tipoDocumentoId;
    private String tipoDocumentoName;
    private String nombre;
    private String apellido;
    private String telefono;
    private String direccion;
    private Boolean activo;
    
    // Campos adicionales del frontend
    private String email;
    private String ciudad;

    // Constructor desde entidad Cliente
    public ClienteDto(Cliente cliente) {
        this.documento = cliente.getId();
        this.nit = cliente.getNit();
        
        if (cliente.getCategory() != null) {
            this.categoryId = cliente.getCategory().getId();
            this.categoryName = cliente.getCategory().getName();
        }
        
        if (cliente.getTipoDocumento() != null) {
            this.tipoDocumentoId = cliente.getTipoDocumento().getId();
            this.tipoDocumentoName = cliente.getTipoDocumento().getName();
        }
        
        this.nombre = cliente.getNombre();
        this.apellido = cliente.getApellido();
        this.telefono = cliente.getTelefono();
        this.direccion = cliente.getDireccion();
        this.activo = cliente.getActivo();
    }
}
