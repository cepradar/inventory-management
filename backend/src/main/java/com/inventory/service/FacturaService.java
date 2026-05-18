package com.inventory.service;

import com.inventory.dto.VentaDetalleDto;
import com.inventory.dto.VentaDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para generación de PDF de Facturas de Venta.
 * El PDF se construye en memoria; no se persiste en disco.
 */
@Service
public class FacturaService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Color COLOR_HEADER_BG  = new Color(30, 64, 175);
    private static final Color COLOR_HEADER_FG  = Color.WHITE;
    private static final Color COLOR_TABLE_HDR  = new Color(59, 130, 246);
    private static final Color COLOR_ROW_ALT    = new Color(249, 250, 251);
    private static final Color COLOR_SECTION_BG = new Color(239, 246, 255);
    private static final Color COLOR_TOTAL_BG   = new Color(220, 252, 231);
    private static final Color COLOR_BORDER     = new Color(203, 213, 225);

    private final VentasService ventaService;

    public FacturaService(VentasService ventaService) {
        this.ventaService = ventaService;
    }

    /**
     * Genera los bytes del PDF de factura para una venta.
     *
     * @param ventaId ID de la venta
     * @return bytes del PDF listo para enviar al cliente
     */
    public byte[] generarFacturaPdf(Long ventaId) throws Exception {
        VentaDto venta = ventaService.obtenerVentaPorId(ventaId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36f, 36f, 60f, 50f);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);

        // Pie de página
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter w, Document d) {
                PdfContentByte cb = w.getDirectContent();
                Font f = new Font(Font.HELVETICA, 7, Font.ITALIC, Color.GRAY);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase("Factura #" + venta.getId() + "  –  "
                                + (venta.getFecha() != null ? venta.getFecha().format(FMT) : "")
                                + "  –  Pág. " + w.getPageNumber(), f),
                        d.getPageSize().getWidth() / 2f, 25f, 0);
            }
        });

        doc.open();

        agregarEncabezado(doc, venta);
        doc.add(Chunk.NEWLINE);

        agregarInfoCliente(doc, venta);
        doc.add(Chunk.NEWLINE);

        agregarTablaProductos(doc, venta.getDetalles());
        doc.add(Chunk.NEWLINE);

        agregarTotalObservaciones(doc, venta);

        doc.close();
        return baos.toByteArray();
    }

    // ── Encabezado ────────────────────────────────────────────────────────────
    private void agregarEncabezado(Document doc, VentaDto venta) throws DocumentException {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{3f, 2f});
        t.setSpacingAfter(4f);

        PdfPCell cEmp = new PdfPCell();
        cEmp.setBorder(Rectangle.NO_BORDER);
        cEmp.setBackgroundColor(COLOR_HEADER_BG);
        cEmp.setPadding(14f);
        Font fNom = new Font(Font.HELVETICA, 18, Font.BOLD, COLOR_HEADER_FG);
        Font fSub = new Font(Font.HELVETICA, 9,  Font.NORMAL, new Color(186, 230, 253));
        cEmp.addElement(new Paragraph("GESTIÓN DE INVENTARIO", fNom));
        cEmp.addElement(new Paragraph("Sistema de Ventas y Facturación", fSub));
        t.addCell(cEmp);

        PdfPCell cFac = new PdfPCell();
        cFac.setBorder(Rectangle.NO_BORDER);
        cFac.setBackgroundColor(COLOR_HEADER_BG);
        cFac.setPadding(14f);
        cFac.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Font fTit = new Font(Font.HELVETICA, 13, Font.BOLD, COLOR_HEADER_FG);
        Font fDat = new Font(Font.HELVETICA, 9,  Font.NORMAL, new Color(186, 230, 253));
        Font fId  = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(253, 224, 71));
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_RIGHT);
        p.add(new Chunk("FACTURA DE VENTA\n", fTit));
        p.add(new Chunk("#" + venta.getId() + "\n", fId));
        p.add(new Chunk("Fecha: " + (venta.getFecha() != null ? venta.getFecha().format(FMT) : "-") + "\n", fDat));
        p.add(new Chunk("Vendedor: " + safe(venta.getUsuarioNombre() != null ? venta.getUsuarioNombre() : venta.getUsuarioUsername()), fDat));
        cFac.addElement(p);
        t.addCell(cFac);
        doc.add(t);
    }

    // ── Info cliente ──────────────────────────────────────────────────────────
    private void agregarInfoCliente(Document doc, VentaDto venta) throws DocumentException {
        addSectionTitle(doc, "DATOS DEL CLIENTE");

        PdfPTable t = new PdfPTable(4);
        t.setWidthPercentage(100);
        t.setSpacingAfter(6f);

        Font fLbl = new Font(Font.HELVETICA, 8, Font.BOLD,   new Color(55, 65, 81));
        Font fVal = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK);

        addCell2(t, "Nombre Comprador", safe(venta.getNombreComprador()),
                 "Teléfono", safe(venta.getTelefonoComprador()), fLbl, fVal);
        addCell2(t, "Email", safe(venta.getEmailComprador()),
                 "Orden de Servicio", safe(venta.getOrdenDeServicioId()), fLbl, fVal);
        doc.add(t);
    }

    // ── Tabla de productos ────────────────────────────────────────────────────
    private void agregarTablaProductos(Document doc, List<VentaDetalleDto> detalles) throws DocumentException {
        addSectionTitle(doc, "DETALLE DE PRODUCTOS");

        PdfPTable t = new PdfPTable(5);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{0.8f, 4f, 1.5f, 2f, 2f});
        t.setSpacingAfter(6f);

        Font fHdr = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
        addHdrCell(t, "#",             fHdr);
        addHdrCell(t, "Producto",      fHdr);
        addHdrCell(t, "Cantidad",      fHdr);
        addHdrCell(t, "Precio Unit.",  fHdr);
        addHdrCell(t, "Subtotal",      fHdr);

        Font fBody = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);
        boolean alt = false;
        int i = 1;

        if (detalles != null) {
            for (VentaDetalleDto d : detalles) {
                Color bg = alt ? COLOR_ROW_ALT : Color.WHITE;
                addBodyCell(t, String.valueOf(i++), fBody, Element.ALIGN_CENTER, bg);
                addBodyCell(t, safe(d.getProductNombre()),          fBody, Element.ALIGN_LEFT,   bg);
                addBodyCell(t, String.valueOf(d.getCantidad() != null ? d.getCantidad() : 0),
                            fBody, Element.ALIGN_CENTER, bg);
                addBodyCell(t, fmt(d.getPrecioUnitario()), fBody, Element.ALIGN_RIGHT, bg);
                addBodyCell(t, fmt(d.getSubtotal()),       fBody, Element.ALIGN_RIGHT, bg);
                alt = !alt;
            }
        }
        doc.add(t);
    }

    // ── Total y observaciones ─────────────────────────────────────────────────
    private void agregarTotalObservaciones(Document doc, VentaDto venta) throws DocumentException {
        // Total
        PdfPTable tTot = new PdfPTable(2);
        tTot.setWidthPercentage(40);
        tTot.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tTot.setWidths(new float[]{3f, 2f});
        tTot.setSpacingAfter(10f);

        Font fTot = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(22, 101, 52));
        PdfPCell cL = new PdfPCell(new Phrase("TOTAL", fTot));
        cL.setPadding(8f);
        cL.setBackgroundColor(COLOR_TOTAL_BG);
        cL.setBorderColor(COLOR_BORDER);
        tTot.addCell(cL);
        PdfPCell cV = new PdfPCell(new Phrase(fmt(venta.getTotalVenta()), fTot));
        cV.setPadding(8f);
        cV.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cV.setBackgroundColor(COLOR_TOTAL_BG);
        cV.setBorderColor(COLOR_BORDER);
        tTot.addCell(cV);
        doc.add(tTot);

        // Observaciones
        if (venta.getObservaciones() != null && !venta.getObservaciones().isBlank()) {
            addSectionTitle(doc, "OBSERVACIONES");
            PdfPTable tObs = new PdfPTable(1);
            tObs.setWidthPercentage(100);
            Font fObs = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);
            PdfPCell c = new PdfPCell(new Phrase(venta.getObservaciones(), fObs));
            c.setPadding(6f);
            c.setBorderColor(COLOR_BORDER);
            tObs.addCell(c);
            doc.add(tObs);
        }

        // Pie legal
        doc.add(Chunk.NEWLINE);
        Font fLeg = new Font(Font.HELVETICA, 7, Font.ITALIC, Color.GRAY);
        Paragraph pLeg = new Paragraph("Este documento es un comprobante de venta generado electrónicamente.", fLeg);
        pLeg.setAlignment(Element.ALIGN_CENTER);
        doc.add(pLeg);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
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

    private void addCell2(PdfPTable t, String l1, String v1, String l2, String v2,
                          Font fL, Font fV) {
        for (String[] pair : new String[][]{{l1, v1}, {l2, v2}}) {
            PdfPCell c = new PdfPCell();
            c.setBackgroundColor(COLOR_SECTION_BG);
            c.setBorderColor(COLOR_BORDER);
            c.setPadding(5f);
            Paragraph p = new Paragraph();
            p.add(new Chunk(pair[0] + ": ", fL));
            p.add(new Chunk(pair[1], fV));
            c.addElement(p);
            t.addCell(c);
        }
    }

    private void addHdrCell(PdfPTable t, String text, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBackgroundColor(COLOR_TABLE_HDR);
        c.setPadding(6f);
        c.setBorderColor(COLOR_BORDER);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        t.addCell(c);
    }

    private void addBodyCell(PdfPTable t, String text, Font f, int alignment, Color bg) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBackgroundColor(bg);
        c.setPadding(5f);
        c.setBorderColor(COLOR_BORDER);
        c.setHorizontalAlignment(alignment);
        t.addCell(c);
    }

    private String safe(String v) { return v != null && !v.isBlank() ? v : "-"; }

    private String fmt(BigDecimal v) {
        if (v == null) return "$ 0,00";
        return String.format("$ %,.2f", v);
    }
}
