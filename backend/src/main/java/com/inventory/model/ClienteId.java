package com.inventory.model;

import java.io.Serializable;
import java.util.Objects;

public class ClienteId implements Serializable {
    private String id;
    private String tipoDocumentoId;

    public ClienteId() {
    }

    public ClienteId(String id, String tipoDocumentoId) {
        this.id = id;
        this.tipoDocumentoId = tipoDocumentoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipoDocumentoId() {
        return tipoDocumentoId;
    }

    public void setTipoDocumentoId(String tipoDocumentoId) {
        this.tipoDocumentoId = tipoDocumentoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteId clienteId = (ClienteId) o;
        return Objects.equals(id, clienteId.id)
                && Objects.equals(tipoDocumentoId, clienteId.tipoDocumentoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tipoDocumentoId);
    }
}
