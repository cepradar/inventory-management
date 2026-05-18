package com.inventory.service;

import com.inventory.dto.OrdenDeServicioDto;
import com.inventory.dto.VentaDetalleDto;
import com.inventory.dto.VentaDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para generación de PDF de Órdenes de Servicio.
 * El PDF se genera en memoria y se devuelve como bytes; no se almacena en disco.
 */
@Service
public class OrdenServicioPdfService {

    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Colores corporativos ──────────────────────────────────────────────────
    private static final Color COLOR_HEADER_BG  = new Color(30, 64, 175);   // azul oscuro
    private static final Color COLOR_HEADER_FG  = Color.WHITE;
    private static final Color COLOR_SECTION_BG = new Color(239, 246, 255); // azul muy claro
    private static final Color COLOR_TABLE_HDR  = new Color(59, 130, 246);  // azul medio
    private static final Color COLOR_ROW_ALT    = new Color(249, 250, 251); // gris muy claro
    private static final Color COLOR_BORDER     = new Color(203, 213, 225); // gris borde
    private static final Color COLOR_TOTAL_BG   = new Color(220, 252, 231); // verde claro
    private static final Color COLOR_DANGER_BG  = new Color(254, 242, 242); // rojo claro

    @Autowired
    private OrdenDeServicioService ordenService;

    @Autowired
    private VentasService ventasService;

    /**
     * Genera el PDF completo de la orden de servicio incluyendo ventas asociadas.
     *
     * @param ordenId ID de la orden de servicio
     * @return bytes del PDF
     */
    public byte[] generarPdf(String ordenId) throws Exception {
        OrdenDeServicioDto orden = ordenService.obtenerServicioPorId(ordenId);
        List<VentaDto> ventas    = ventasService.obtenerVentasPorOrden(ordenId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36f, 36f, 60f, 50f);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);

        // Pie de página con número de página
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter w, Document d) {
                PdfContentByte cb = w.getDirectContent();
                Font f = new Font(Font.HELVETICA, 7, Font.ITALIC, Color.GRAY);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase("Orden de Servicio #" + ordenId + "  –  Generado: "
                                + java.time.LocalDateTime.now().format(FMT_DATETIME)
                                + "  –  Pág. " + w.getPageNumber(), f),
                        d.getPageSize().getWidth() / 2f, 25f, 0);
            }
        });

        doc.open();

        agregarEncabezado(doc, orden);
        doc.add(Chunk.NEWLINE);

        addSectionTitle(doc, "DATOS DEL CLIENTE");
        agregarSeccionCliente(doc, orden);
        addSectionTitle(doc, "DATOS DEL EQUIPO");
        agregarSeccionEquipo(doc, orden);
        doc.add(Chunk.NEWLINE);

        addSectionTitle(doc, "INFORMACIÓN DEL SERVICIO");
        agregarSeccionServicio(doc, orden);
        doc.add(Chunk.NEWLINE);

        addSectionTitle(doc, "INFORME TÉCNICO");
        agregarSeccionTecnica(doc, orden);
        doc.add(Chunk.NEWLINE);

        if (!ventas.isEmpty()) {
            addSectionTitle(doc, "PRODUCTOS Y REPUESTOS UTILIZADOS");
            agregarTablaProductos(doc, ventas);
            doc.add(Chunk.NEWLINE);
        }

        addSectionTitle(doc, "RESUMEN DE COSTOS");
        agregarResumenCostos(doc, orden, ventas);
        doc.add(Chunk.NEWLINE);

        addSectionTitle(doc, "CONFORMIDAD Y FIRMAS");
        agregarSeccionFirma(doc, orden);

        doc.close();
        return baos.toByteArray();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Encabezado principal
    // ──────────────────────────────────────────────────────────────────────────
    private void agregarEncabezado(Document doc, OrdenDeServicioDto orden) throws DocumentException {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3f, 2f});
        tabla.setSpacingAfter(4f);

        // Bloque izquierdo – nombre empresa
        PdfPCell celdaNombre = new PdfPCell();
        celdaNombre.setBorder(Rectangle.NO_BORDER);
        celdaNombre.setBackgroundColor(COLOR_HEADER_BG);
        celdaNombre.setPadding(14f);

        Font fEmpresa = new Font(Font.HELVETICA, 18, Font.BOLD, COLOR_HEADER_FG);
        Font fSlogan  = new Font(Font.HELVETICA, 9,  Font.NORMAL, new Color(186, 230, 253));
        celdaNombre.addElement(new Paragraph("GESTIÓN DE INVENTARIO", fEmpresa));
        celdaNombre.addElement(new Paragraph("Sistema de Órdenes de Servicio Técnico", fSlogan));
        tabla.addCell(celdaNombre);

        // Bloque derecho – datos de la orden
        PdfPCell celdaOrden = new PdfPCell();
        celdaOrden.setBorder(Rectangle.NO_BORDER);
        celdaOrden.setBackgroundColor(COLOR_HEADER_BG);
        celdaOrden.setPadding(14f);
        celdaOrden.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font fTituloDoc = new Font(Font.HELVETICA, 13, Font.BOLD, COLOR_HEADER_FG);
        Font fDatoDoc   = new Font(Font.HELVETICA, 9,  Font.NORMAL, new Color(186, 230, 253));
        Font fIdDoc     = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(253, 224, 71));

        Paragraph parDocumento = new Paragraph();
        parDocumento.setAlignment(Element.ALIGN_RIGHT);
        parDocumento.add(new Chunk("ORDEN DE SERVICIO\n", fTituloDoc));
        parDocumento.add(new Chunk("#" + orden.getId() + "\n", fIdDoc));

        String fechaIngreso = orden.getFechaIngreso() != null
                ? orden.getFechaIngreso().format(FMT_DATETIME) : "-";
        parDocumento.add(new Chunk("Ingreso: " + fechaIngreso + "\n", fDatoDoc));
        parDocumento.add(new Chunk("Estado: " + safe(orden.getEstado()), fDatoDoc));

        celdaOrden.addElement(parDocumento);
        tabla.addCell(celdaOrden);

        doc.add(tabla);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Sección cliente
    // ──────────────────────────────────────────────────────────────────────────
    private void agregarSeccionCliente(Document doc, OrdenDeServicioDto orden) throws DocumentException {
        PdfPTable t = new PdfPTable(4);
        t.setWidthPercentage(100);
        t.setSpacingAfter(6f);

        String nombreCompleto = trim(orden.getClienteNombre()) + " " + trim(orden.getClienteApellido());
        agregarFila(t, "Nombre", nombreCompleto.trim(),
                "Documento", trim(orden.getClienteId()) + " (" + trim(orden.getClienteTipoDocumentoId()) + ")");
        agregarFila(t, "Teléfono", safe(orden.getClienteTelefono()),
                "Email", safe(orden.getClienteEmail()));
        doc.add(t);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Sección equipo / electrodoméstico
    // ──────────────────────────────────────────────────────────────────────────
    private void agregarSeccionEquipo(Document doc, OrdenDeServicioDto orden) throws DocumentException {
        PdfPTable t = new PdfPTable(4);
        t.setWidthPercentage(100);
        t.setSpacingAfter(6f);

        agregarFila(t, "Tipo", safe(orden.getElectrodomesticoTipo()),
                "Marca", safe(orden.getElectrodomesticoMarca()));
        agregarFila(t, "Modelo", safe(orden.getElectrodomesticoModelo()),
                "ID Equipo", orden.getElectrodomesticoId() != null ? orden.getElectrodomesticoId().toString() : "-");
        doc.add(t);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Sección información del servicio
    // ──────────────────────────────────────────────────────────────────────────
    private void agregarSeccionServicio(Document doc, OrdenDeServicioDto orden) throws DocumentException {
        PdfPTable t = new PdfPTable(4);
        t.setWidthPercentage(100);
        t.setSpacingAfter(6f);

        agregarFila(t, "Tipo de Servicio", safe(orden.getTipoServicio()),
                "Técnico Asignado", safe(orden.getTecnicoAsignadoNombre() != null
                        ? orden.getTecnicoAsignadoNombre() : orden.getTecnicoAsignadoUsername()));

        String fechaAsig = orden.getFechaAsignacion() != null
                ? orden.getFechaAsignacion().format(FMT_DATETIME) : "-";
        String fechaSal  = orden.getFechaSalida() != null
                ? orden.getFechaSalida().format(FMT_DATETIME) : "-";
        agregarFila(t, "Fecha Asignación", fechaAsig, "Fecha Salida", fechaSal);
        doc.add(t);

        // Descripción del problema – celda ancha
        PdfPTable tDesc = new PdfPTable(1);
        tDesc.setWidthPercentage(100);
        tDesc.setSpacingAfter(6f);
        agregarCeldaAncha(tDesc, "Descripción del Problema", safe(orden.getDescripcionProblema()));
        doc.add(tDesc);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Sección técnica (diagnóstico, solución, repuestos)
    // ──────────────────────────────────────────────────────────────────────────
    private void agregarSeccionTecnica(Document doc, OrdenDeServicioDto orden) throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingAfter(6f);

        agregarCeldaAncha(t, "Diagnóstico", safe(orden.getDiagnostico()));
        agregarCeldaAncha(t, "Solución Aplicada", safe(orden.getSolucion()));
        agregarCeldaAncha(t, "Repuestos / Partes Cambiadas", safe(orden.getPartesCambiadas()));
        if (orden.getObservaciones() != null && !orden.getObservaciones().isBlank()) {
            agregarCeldaAncha(t, "Observaciones", orden.getObservaciones());
        }
        doc.add(t);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Tabla de productos / ventas asociados
    // ──────────────────────────────────────────────────────────────────────────
    private void agregarTablaProductos(Document doc, List<VentaDto> ventas) throws DocumentException {
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1f, 4f, 1.5f, 2f, 2f});
        tabla.setSpacingAfter(6f);

        // Cabecera
        Font fHdr = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
        agregarCeldaTablaHdr(tabla, "#",          fHdr);
        agregarCeldaTablaHdr(tabla, "Producto",   fHdr);
        agregarCeldaTablaHdr(tabla, "Cantidad",   fHdr);
        agregarCeldaTablaHdr(tabla, "Precio Unit.", fHdr);
        agregarCeldaTablaHdr(tabla, "Subtotal",   fHdr);

        Font fBody = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);
        int idx = 1;
        boolean altRow = false;
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (VentaDto venta : ventas) {
            if (venta.getDetalles() == null) continue;
            for (VentaDetalleDto d : venta.getDetalles()) {
                Color rowBg = altRow ? COLOR_ROW_ALT : Color.WHITE;
                agregarCeldaTablaBody(tabla, String.valueOf(idx++), fBody, Element.ALIGN_CENTER, rowBg);
                agregarCeldaTablaBody(tabla, safe(d.getProductNombre()),  fBody, Element.ALIGN_LEFT,   rowBg);
                agregarCeldaTablaBody(tabla, String.valueOf(d.getCantidad() != null ? d.getCantidad() : 0),
                        fBody, Element.ALIGN_CENTER, rowBg);
                agregarCeldaTablaBody(tabla, formatMoneda(d.getPrecioUnitario()), fBody, Element.ALIGN_RIGHT, rowBg);
                agregarCeldaTablaBody(tabla, formatMoneda(d.getSubtotal()),       fBody, Element.ALIGN_RIGHT, rowBg);

                if (d.getSubtotal() != null) grandTotal = grandTotal.add(d.getSubtotal());
                altRow = !altRow;
            }
        }

        // Fila total
        Font fTot = new Font(Font.HELVETICA, 9, Font.BOLD, new Color(22, 101, 52));
        PdfPCell cTotLabel = new PdfPCell(new Phrase("TOTAL PRODUCTOS", fTot));
        cTotLabel.setColspan(4);
        cTotLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cTotLabel.setPadding(6f);
        cTotLabel.setBackgroundColor(COLOR_TOTAL_BG);
        cTotLabel.setBorderColor(COLOR_BORDER);
        tabla.addCell(cTotLabel);

        PdfPCell cTotVal = new PdfPCell(new Phrase(formatMoneda(grandTotal), fTot));
        cTotVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cTotVal.setPadding(6f);
        cTotVal.setBackgroundColor(COLOR_TOTAL_BG);
        cTotVal.setBorderColor(COLOR_BORDER);
        tabla.addCell(cTotVal);

        doc.add(tabla);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Resumen de costos (servicio + repuestos + garantía)
    // ──────────────────────────────────────────────────────────────────────────
    private void agregarResumenCostos(Document doc, OrdenDeServicioDto orden,
                                      List<VentaDto> ventas) throws DocumentException {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(60);
        t.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.setSpacingAfter(6f);
        t.setWidths(new float[]{3f, 2f});

        Font fLabel = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY);
        Font fValue = new Font(Font.HELVETICA, 9, Font.BOLD,   Color.DARK_GRAY);
        Font fTotal = new Font(Font.HELVETICA, 10, Font.BOLD,  new Color(22, 101, 52));

        agregarFilaCosto(t, "Costo de Servicio",  formatMoneda(orden.getCostoServicio()),  fLabel, fValue);
        agregarFilaCosto(t, "Costo de Repuestos", formatMoneda(orden.getCostoRepuestos()), fLabel, fValue);

        BigDecimal totalVentas = ventas.stream()
                .map(v -> v.getTotalVenta() != null ? v.getTotalVenta() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalVentas.compareTo(BigDecimal.ZERO) > 0) {
            agregarFilaCosto(t, "Total Ventas Registradas", formatMoneda(totalVentas), fLabel, fValue);
        }

        // Separador y total general
        PdfPCell cSepL = new PdfPCell(new Phrase("TOTAL GENERAL", fTotal));
        PdfPCell cSepV = new PdfPCell(new Phrase(formatMoneda(orden.getTotalCosto()), fTotal));
        cSepL.setPadding(7f);
        cSepV.setPadding(7f);
        cSepL.setHorizontalAlignment(Element.ALIGN_LEFT);
        cSepV.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cSepL.setBackgroundColor(COLOR_TOTAL_BG);
        cSepV.setBackgroundColor(COLOR_TOTAL_BG);
        cSepL.setBorderColor(COLOR_BORDER);
        cSepV.setBorderColor(COLOR_BORDER);
        t.addCell(cSepL);
        t.addCell(cSepV);

        // Garantía
        if (orden.getGarantiaServicio() != null && orden.getGarantiaServicio() > 0) {
            Font fGar = new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(30, 64, 175));
            String txtGar = "Garantía: " + orden.getGarantiaServicio() + " días";
            if (orden.getVencimientoGarantia() != null) {
                txtGar += "  (vence: " + orden.getVencimientoGarantia().format(FMT_DATE) + ")";
            }
            PdfPCell cGar = new PdfPCell(new Phrase(txtGar, fGar));
            cGar.setColspan(2);
            cGar.setPadding(5f);
            cGar.setBackgroundColor(new Color(239, 246, 255));
            cGar.setBorderColor(COLOR_BORDER);
            t.addCell(cGar);
        }
        doc.add(t);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Sección de firmas
    // ──────────────────────────────────────────────────────────────────────────
    private void agregarSeccionFirma(Document doc, OrdenDeServicioDto orden) throws DocumentException {
        PdfPTable t = new PdfPTable(3);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{1f, 1f, 1f});
        t.setSpacingAfter(6f);

        Font fFirma   = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);
        Font fNomFirma = new Font(Font.HELVETICA, 8, Font.BOLD, Color.DARK_GRAY);

        String nomCliente  = trim(orden.getClienteNombre()) + " " + trim(orden.getClienteApellido());
        String nomTecnico  = orden.getTecnicoAsignadoNombre() != null
                ? orden.getTecnicoAsignadoNombre() : safe(orden.getTecnicoAsignadoUsername());

        agregarCeldaFirma(t, "Firma del Cliente", nomCliente.trim(), fFirma, fNomFirma);
        agregarCeldaFirma(t, "Firma del Técnico", nomTecnico, fFirma, fNomFirma);
        agregarCeldaFirma(t, "Sello / Autorización", "Empresa", fFirma, fNomFirma);

        doc.add(t);

        Font fLegal = new Font(Font.HELVETICA, 7, Font.ITALIC, Color.GRAY);
        Paragraph pLegal = new Paragraph(
                "Al firmar este documento el cliente acepta los trabajos realizados y las condiciones de la garantía establecida. " +
                "Este documento es válido como comprobante de servicio técnico.", fLegal);
        pLegal.setAlignment(Element.ALIGN_CENTER);
        doc.add(pLegal);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Helpers de construcción de celdas / párrafos
    // ──────────────────────────────────────────────────────────────────────────

    /** Agrega un separador coloreado como encabezado de sección. */
    private void addSectionTitle(Document doc, String texto) throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingBefore(8f);
        t.setSpacingAfter(3f);
        Font f = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setBackgroundColor(COLOR_TABLE_HDR);
        c.setPadding(5f);
        c.setBorder(Rectangle.NO_BORDER);
        t.addCell(c);
        doc.add(t);
    }

    private void agregarFila(PdfPTable t, String lbl1, String val1, String lbl2, String val2) {
        Font fLbl = new Font(Font.HELVETICA, 8, Font.BOLD, new Color(55, 65, 81));
        Font fVal = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK);
        addLabelValueCell(t, lbl1, val1, fLbl, fVal);
        addLabelValueCell(t, lbl2, val2, fLbl, fVal);
    }

    private void addLabelValueCell(PdfPTable t, String label, String value,
                                   Font fLabel, Font fValue) {
        PdfPCell c = new PdfPCell();
        c.setBorderColor(COLOR_BORDER);
        c.setBackgroundColor(COLOR_SECTION_BG);
        c.setPadding(5f);
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + ": ", fLabel));
        p.add(new Chunk(value, fValue));
        c.addElement(p);
        t.addCell(c);
    }

    private void agregarCeldaAncha(PdfPTable t, String label, String value) {
        Font fLbl = new Font(Font.HELVETICA, 8, Font.BOLD, new Color(55, 65, 81));
        Font fVal = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK);
        PdfPCell c = new PdfPCell();
        c.setBorderColor(COLOR_BORDER);
        c.setBackgroundColor(Color.WHITE);
        c.setPadding(6f);
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", fLbl));
        p.add(new Chunk(value.isEmpty() ? "(sin información)" : value, fVal));
        c.addElement(p);
        t.addCell(c);
    }

    private void agregarCeldaTablaHdr(PdfPTable t, String texto, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setBackgroundColor(COLOR_TABLE_HDR);
        c.setPadding(6f);
        c.setBorderColor(COLOR_BORDER);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        t.addCell(c);
    }

    private void agregarCeldaTablaBody(PdfPTable t, String texto, Font f,
                                       int alignment, Color bg) {
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setBackgroundColor(bg);
        c.setPadding(5f);
        c.setBorderColor(COLOR_BORDER);
        c.setHorizontalAlignment(alignment);
        t.addCell(c);
    }

    private void agregarFilaCosto(PdfPTable t, String label, String value,
                                  Font fLabel, Font fValue) {
        PdfPCell cL = new PdfPCell(new Phrase(label, fLabel));
        cL.setPadding(5f);
        cL.setBorderColor(COLOR_BORDER);
        cL.setBackgroundColor(COLOR_SECTION_BG);
        t.addCell(cL);

        PdfPCell cV = new PdfPCell(new Phrase(value, fValue));
        cV.setPadding(5f);
        cV.setBorderColor(COLOR_BORDER);
        cV.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cV.setBackgroundColor(COLOR_SECTION_BG);
        t.addCell(cV);
    }

    private void agregarCeldaFirma(PdfPTable t, String titulo, String nombre,
                                   Font fBase, Font fNombre) {
        PdfPCell c = new PdfPCell();
        c.setPadding(8f);
        c.setMinimumHeight(70f);
        c.setBorderColor(COLOR_BORDER);
        c.setBackgroundColor(Color.WHITE);

        Paragraph p = new Paragraph();
        p.add(new Chunk(titulo + "\n\n\n", fBase));
        p.add(new Chunk("_________________________________\n", fBase));
        p.add(new Chunk(nombre, fNombre));
        p.setAlignment(Element.ALIGN_CENTER);
        c.addElement(p);
        t.addCell(c);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Utilidades
    // ──────────────────────────────────────────────────────────────────────────

    private String safe(String value) {
        return value != null && !value.isBlank() ? value : "-";
    }

    private String trim(String value) {
        return value != null ? value.trim() : "";
    }

    private String formatMoneda(BigDecimal value) {
        if (value == null) return "$ 0,00";
        return String.format("$ %,.2f", value);
    }
}
