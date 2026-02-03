package com.inventory.model;

import java.io.Serializable;
import java.util.Objects;

public class ServicioReparacionProductoId implements Serializable {

    private String servicioReparacion;
    private Integer regProd;

    public ServicioReparacionProductoId() {
    }

    public ServicioReparacionProductoId(String servicioReparacion, Integer regProd) {
        this.servicioReparacion = servicioReparacion;
        this.regProd = regProd;
    }

    public String getServicioReparacion() {
        return servicioReparacion;
    }

    public void setServicioReparacion(String servicioReparacion) {
        this.servicioReparacion = servicioReparacion;
    }

    public Integer getRegProd() {
        return regProd;
    }

    public void setRegProd(Integer regProd) {
        this.regProd = regProd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServicioReparacionProductoId that = (ServicioReparacionProductoId) o;
        return Objects.equals(servicioReparacion, that.servicioReparacion)
                && Objects.equals(regProd, that.regProd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(servicioReparacion, regProd);
    }
}
