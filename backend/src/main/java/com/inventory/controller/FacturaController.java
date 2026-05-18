package com.inventory.controller;

import com.inventory.service.DocumentoGeneradorService;
import com.inventory.service.VentasService;
import com.inventory.dto.VentaDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final DocumentoGeneradorService documentoGeneradorService;
    private final VentasService ventasService;

    public FacturaController(DocumentoGeneradorService documentoGeneradorService,
                             VentasService ventasService) {
        this.documentoGeneradorService = documentoGeneradorService;
        this.ventasService = ventasService;
    }

    /**
     * Genera y descarga la factura PDF de una venta usando JasperReports.
     * ADMIN puede descargar cualquier venta; TECNICO solo las suyas.
     * El PDF se genera en memoria y no se almacena en disco.
     */
    @GetMapping("/pdf/{ventaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<byte[]> generarFactura(@PathVariable Long ventaId, Authentication auth) {
        try {
            boolean isTecnico = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));
            if (isTecnico) {
                VentaDto venta = ventasService.obtenerVentaPorId(ventaId);
                if (!venta.getUsuarioUsername().equals(auth.getName())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            byte[] pdf = documentoGeneradorService.generarFactura(ventaId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "factura-" + ventaId + ".pdf");
            headers.setContentLength(pdf.length);
            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
