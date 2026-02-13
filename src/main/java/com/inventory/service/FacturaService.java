package com.inventory.service;

import com.inventory.dto.VentaDto;
import com.inventory.dto.VentaDetalleDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class FacturaService {

    private final VentasService ventaService;

    public FacturaService(VentasService ventaService) {
        this.ventaService = ventaService;
    }

    // ...existing code...
    public byte[] generarFacturaPdf(Long ventaId) throws Exception {
        VentaDto ventaDto = ventaService.obtenerVentaPorId(ventaId);

        // Extraer datos del comprador desde VentaDto
        String nombreComprador = ventaDto.getNombreComprador();
        String emailComprador = ventaDto.getEmailComprador();

        List<VentaDetalleDto> productos = ventaDto.getDetalles();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        document.add(new Paragraph("Factura de Venta #" + ventaDto.getId()));
        document.add(new Paragraph("Fecha: " + ventaDto.getFecha()));
        document.add(new Paragraph("Cliente: " + nombreComprador + " (" + emailComprador + ")"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.addCell("Producto");
        table.addCell("Cantidad");
        table.addCell("Precio unitario");
        table.addCell("Subtotal");

        double total = 0;
        for (VentaDetalleDto pv : productos) {
            table.addCell(pv.getProductNombre());
            table.addCell(String.valueOf(pv.getCantidad()));
            table.addCell(String.format("$ %.2f", pv.getPrecioUnitario()));
            double subtotal = pv.getCantidad() * pv.getPrecioUnitario().doubleValue();
            table.addCell(String.format("$ %.2f", subtotal));
            total += subtotal;
        }

        document.add(table);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total: $ " + String.format("%.2f", total)));

        document.close();
        return baos.toByteArray();
    }
}