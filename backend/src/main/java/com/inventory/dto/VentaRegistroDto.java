package com.inventory.dto;

import java.util.List;

public class VentaRegistroDto {
    /** Número de documento del cliente (parte 1 de la llave compuesta). */
    private String clienteId;
    /** Tipo de documento del cliente (parte 2 de la llave compuesta). */
    private String clienteTipoDocumento;
    private String usuarioUsername;
    private String observaciones;
    private String ordenDeServicioId;
    private List<VentaDetalleRegistroDto> detalles;

    public VentaRegistroDto() {}

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getClienteTipoDocumento() { return clienteTipoDocumento; }
    public void setClienteTipoDocumento(String clienteTipoDocumento) { this.clienteTipoDocumento = clienteTipoDocumento; }

    public String getUsuarioUsername() { return usuarioUsername; }
    public void setUsuarioUsername(String usuarioUsername) { this.usuarioUsername = usuarioUsername; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getOrdenDeServicioId() { return ordenDeServicioId; }
    public void setOrdenDeServicioId(String ordenDeServicioId) { this.ordenDeServicioId = ordenDeServicioId; }

    public List<VentaDetalleRegistroDto> getDetalles() { return detalles; }
    public void setDetalles(List<VentaDetalleRegistroDto> detalles) { this.detalles = detalles; }
}


