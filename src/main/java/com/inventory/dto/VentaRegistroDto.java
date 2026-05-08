package com.inventory.dto;

import java.util.List;

public class VentaRegistroDto {
    private String nombreComprador;
    private String telefonoComprador;
    private String emailComprador;
    private String usuarioUsername;
    private String observaciones;
    private List<VentaDetalleRegistroDto> detalles;

    public VentaRegistroDto() {}

    public String getNombreComprador() { return nombreComprador; }
    public void setNombreComprador(String nombreComprador) { this.nombreComprador = nombreComprador; }

    public String getTelefonoComprador() { return telefonoComprador; }
    public void setTelefonoComprador(String telefonoComprador) { this.telefonoComprador = telefonoComprador; }

    public String getEmailComprador() { return emailComprador; }
    public void setEmailComprador(String emailComprador) { this.emailComprador = emailComprador; }

    public String getUsuarioUsername() { return usuarioUsername; }
    public void setUsuarioUsername(String usuarioUsername) { this.usuarioUsername = usuarioUsername; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public List<VentaDetalleRegistroDto> getDetalles() { return detalles; }
    public void setDetalles(List<VentaDetalleRegistroDto> detalles) { this.detalles = detalles; }
}


