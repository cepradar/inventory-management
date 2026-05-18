package com.inventory.service;

import com.inventory.dto.OrdenDeServicioDto;
import com.inventory.dto.VentaDetalleDto;
import com.inventory.dto.VentaDto;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio central de JasperReports.
 * Gestiona compilación, caché y exportación de plantillas JRXML a PDF.
 */
@Service
public class JasperReportService {

    private static final Logger log = LoggerFactory.getLogger(JasperReportService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Caché de plantillas compiladas del sistema (classpath) */
    private final Map<String, JasperReport> systemReportCache = new ConcurrentHashMap<>();

    private final ReportStorageService storageService;

    public JasperReportService(ReportStorageService storageService) {
        this.storageService = storageService;
    }

    // ── Compilación ──────────────────────────────────────────────────────────

    /**
     * Compila un JRXML dado como bytes.
     *
     * @param jrxmlBytes contenido del archivo .jrxml
     * @return JasperReport compilado
     */
    public JasperReport compileJrxml(byte[] jrxmlBytes) throws JRException {
        try (InputStream is = new ByteArrayInputStream(jrxmlBytes)) {
            return JasperCompileManager.compileReport(is);
        } catch (Exception e) {
            throw new JRException("Error al compilar el JRXML: " + e.getMessage(), e);
        }
    }

    /**
     * Carga la plantilla del sistema desde el classpath.
     * Compila el .jrxml en primera llamada y lo cachea para llamadas posteriores.
     *
     * @param templateName nombre base sin extensión (ej: "factura")
     */
    public JasperReport getSystemTemplate(String templateName) throws JRException {
        return systemReportCache.computeIfAbsent(templateName, name -> {
            try {
                String resourcePath = "/reports/" + name + ".jrxml";
                InputStream is = JasperReportService.class.getResourceAsStream(resourcePath);
                if (is == null) {
                    throw new RuntimeException("Plantilla del sistema no encontrada en classpath: " + resourcePath);
                }
                log.info("Compilando plantilla del sistema: {}", resourcePath);
                return JasperCompileManager.compileReport(is);
            } catch (JRException e) {
                throw new RuntimeException("Error compilando plantilla " + name + ": " + e.getMessage(), e);
            }
        });
    }

    /**
     * Invalida la caché para que las plantillas se recompilen en la próxima llamada.
     * Útil después de actualizar plantillas del sistema.
     */
    public void invalidateCache(String templateName) {
        systemReportCache.remove(templateName);
    }

    /**
     * Genera una vista previa de un archivo .jasper precompilado (bytes).
     */
    public byte[] generarPreviewDesdeJasperBytes(byte[] jasperBytes, String tipoReporte) throws JRException {
        try {
            JasperReport report = (JasperReport) net.sf.jasperreports.engine.util.JRLoader
                    .loadObject(new java.io.ByteArrayInputStream(jasperBytes));
            Map<String, Object> params = buildSampleParams(tipoReporte);
            return exportToPdf(report, params, new JREmptyDataSource(1));
        } catch (Exception e) {
            throw new JRException("Error generando preview desde JASPER: " + e.getMessage(), e);
        }
    }

    /**
     * Expone parámetros de muestra públicamente para uso externo.
     */
    public Map<String, Object> getSampleParams(String tipoReporte) {
        return buildSampleParams(tipoReporte);
    }

    // ── Exportación ──────────────────────────────────────────────────────────

    /**
     * Rellena una plantilla compilada con parámetros y datasource, y exporta a PDF.
     */
    public byte[] exportToPdf(JasperReport report, Map<String, Object> parameters,
                               JRDataSource dataSource) throws JRException {
        JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);
        return JasperExportManager.exportReportToPdf(print);
    }

    /**
     * Exporta con configuración PDF avanzada (compresión, metadatos).
     */
    public byte[] exportToPdfAdvanced(JasperReport report, Map<String, Object> parameters,
                                       JRDataSource dataSource, String title) throws JRException {
        JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

        SimplePdfExporterConfiguration config = new SimplePdfExporterConfiguration();
        config.setCompressed(true);
        if (title != null) {
            config.setPdfJavaScript(null);
        }
        exporter.setConfiguration(config);
        exporter.exportReport();

        return baos.toByteArray();
    }

    // ── Generadores específicos ───────────────────────────────────────────────

    /**
     * Genera PDF de factura usando la plantilla del sistema.
     *
     * @param venta  datos de la venta
     * @param empresa nombre y datos de la empresa (puede ser null)
     * @return bytes del PDF
     */
    public byte[] generarFactura(VentaDto venta, Map<String, String> empresa) throws JRException {
        JasperReport template = getSystemTemplate("factura");

        Map<String, Object> params = buildFacturaParams(venta, empresa);

        List<VentaDetalleDto> detalles = venta.getDetalles() != null
                ? venta.getDetalles()
                : Collections.emptyList();

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(detalles);
        return exportToPdf(template, params, dataSource);
    }

    /**
     * Genera PDF de factura usando una plantilla personalizada del sistema de almacenamiento.
     */
    public byte[] generarFacturaConPlantilla(VentaDto venta, Map<String, String> empresa,
                                              String jrxmlNombre) throws Exception {
        byte[] jrxmlBytes = storageService.readFile(jrxmlNombre);
        JasperReport report = compileJrxml(jrxmlBytes);

        Map<String, Object> params = buildFacturaParams(venta, empresa);
        List<VentaDetalleDto> detalles = venta.getDetalles() != null
                ? venta.getDetalles()
                : Collections.emptyList();

        return exportToPdf(report, params, new JRBeanCollectionDataSource(detalles));
    }

    /**
     * Genera PDF de orden de servicio usando la plantilla del sistema.
     */
    public byte[] generarOrdenServicio(OrdenDeServicioDto orden, Map<String, String> empresa) throws JRException {
        JasperReport template = getSystemTemplate("orden_servicio");
        Map<String, Object> params = buildOrdenParams(orden, empresa);
        return exportToPdf(template, params, new JREmptyDataSource(1));
    }

    /**
     * Genera PDF de orden de servicio usando una plantilla personalizada.
     */
    public byte[] generarOrdenServicioConPlantilla(OrdenDeServicioDto orden, Map<String, String> empresa,
                                                    String jrxmlNombre) throws Exception {
        byte[] jrxmlBytes = storageService.readFile(jrxmlNombre);
        JasperReport report = compileJrxml(jrxmlBytes);
        Map<String, Object> params = buildOrdenParams(orden, empresa);
        return exportToPdf(report, params, new JREmptyDataSource(1));
    }

    /**
     * Genera una vista previa de una plantilla con datos de muestra.
     */
    public byte[] generarPreview(byte[] jrxmlBytes, String tipoReporte) throws JRException {
        JasperReport report = compileJrxml(jrxmlBytes);
        Map<String, Object> params = buildSampleParams(tipoReporte);

        JRDataSource dataSource;
        if ("FACTURA".equalsIgnoreCase(tipoReporte)) {
            List<VentaDetalleDto> sampleItems = buildSampleDetalles();
            dataSource = new JRBeanCollectionDataSource(sampleItems);
        } else {
            dataSource = new JREmptyDataSource(1);
        }

        return exportToPdf(report, params, dataSource);
    }

    // ── Constructores de parámetros ───────────────────────────────────────────

    private Map<String, Object> buildFacturaParams(VentaDto venta, Map<String, String> empresa) {
        Map<String, Object> p = new HashMap<>();
        // Empresa
        p.put("EMPRESA_NOMBRE", safeGet(empresa, "nombre", "Mi Empresa"));
        p.put("EMPRESA_NIT", safeGet(empresa, "nit", ""));
        p.put("EMPRESA_DIRECCION", safeGet(empresa, "direccion", ""));
        p.put("EMPRESA_TELEFONO", safeGet(empresa, "telefono", ""));
        p.put("EMPRESA_EMAIL", safeGet(empresa, "email", ""));
        // Venta
        p.put("VENTA_ID", venta.getId() != null ? venta.getId().toString() : "");
        p.put("VENTA_FECHA", venta.getFecha() != null ? venta.getFecha().format(FMT) : "");
        p.put("VENTA_TOTAL", formatMonto(venta.getTotalVenta()));
        p.put("VENTA_OBSERVACIONES", nvl(venta.getObservaciones()));
        // Cliente
        p.put("CLIENTE_NOMBRE", nvl(venta.getNombreComprador()));
        p.put("CLIENTE_DOCUMENTO", nvl(venta.getClienteId()));
        p.put("CLIENTE_TELEFONO", nvl(venta.getTelefonoComprador()));
        p.put("CLIENTE_EMAIL", nvl(venta.getEmailComprador()));
        // Vendedor
        p.put("VENDEDOR_NOMBRE", nvl(venta.getUsuarioNombre()));
        return p;
    }

    private Map<String, Object> buildOrdenParams(OrdenDeServicioDto orden, Map<String, String> empresa) {
        Map<String, Object> p = new HashMap<>();
        // Empresa
        p.put("EMPRESA_NOMBRE", safeGet(empresa, "nombre", "Mi Empresa"));
        p.put("EMPRESA_NIT", safeGet(empresa, "nit", ""));
        p.put("EMPRESA_TELEFONO", safeGet(empresa, "telefono", ""));
        // Orden
        p.put("ORDEN_ID", nvl(orden.getId()));
        p.put("ORDEN_ESTADO", nvl(orden.getEstado()));
        p.put("ORDEN_FECHA_INGRESO", orden.getFechaIngreso() != null ? orden.getFechaIngreso().format(FMT) : "");
        p.put("ORDEN_FECHA_SALIDA", orden.getFechaSalida() != null ? orden.getFechaSalida().format(FMT) : "Pendiente");
        p.put("ORDEN_GARANTIA", orden.getGarantiaServicio() != null
                ? orden.getGarantiaServicio() + " días" : "");
        // Cliente
        String clienteNombre = trim(orden.getClienteNombre()) + " " + trim(orden.getClienteApellido());
        p.put("CLIENTE_NOMBRE", clienteNombre.trim());
        p.put("CLIENTE_DOCUMENTO", nvl(orden.getClienteId()));
        p.put("CLIENTE_TELEFONO", nvl(orden.getClienteTelefono()));
        p.put("CLIENTE_EMAIL", nvl(orden.getClienteEmail()));
        // Equipo
        p.put("EQUIPO_TIPO", nvl(orden.getElectrodomesticoTipo()));
        p.put("EQUIPO_MARCA", nvl(orden.getElectrodomesticoMarca()));
        p.put("EQUIPO_MODELO", nvl(orden.getElectrodomesticoModelo()));
        p.put("EQUIPO_SERIE", orden.getElectrodomesticoId() != null ? orden.getElectrodomesticoId().toString() : "");
        p.put("TIPO_SERVICIO", nvl(orden.getTipoServicio()));
        // Diagnóstico / Solución
        p.put("DESCRIPCION_PROBLEMA", nvl(orden.getDescripcionProblema()));
        p.put("DIAGNOSTICO", nvl(orden.getDiagnostico()));
        p.put("SOLUCION", nvl(orden.getSolucion()));
        p.put("PARTES_CAMBIADAS", nvl(orden.getPartesCambiadas()));
        // Costos
        p.put("COSTO_SERVICIO", formatMonto(orden.getCostoServicio()));
        p.put("TOTAL_COSTO", formatMonto(orden.getTotalCosto()));
        // Técnico / Recepcionista
        p.put("TECNICO_NOMBRE", nvl(orden.getTecnicoAsignadoNombre()));
        p.put("RECEPCIONISTA_NOMBRE", nvl(orden.getUsuarioNombre()));
        p.put("OBSERVACIONES", nvl(orden.getObservaciones()));
        return p;
    }

    private Map<String, Object> buildSampleParams(String tipoReporte) {
        Map<String, Object> p = new HashMap<>();
        if ("FACTURA".equalsIgnoreCase(tipoReporte)) {
            p.put("EMPRESA_NOMBRE", "Empresa de Ejemplo S.A.S.");
            p.put("EMPRESA_NIT", "900.123.456-7");
            p.put("EMPRESA_DIRECCION", "Calle Principal #123, Ciudad");
            p.put("EMPRESA_TELEFONO", "+57 300 123 4567");
            p.put("EMPRESA_EMAIL", "contacto@empresa.com");
            p.put("VENTA_ID", "DEMO-001");
            p.put("VENTA_FECHA", "01/01/2026 10:00");
            p.put("VENTA_TOTAL", "$ 350.000,00");
            p.put("VENTA_OBSERVACIONES", "Vista previa de ejemplo");
            p.put("CLIENTE_NOMBRE", "Juan Pérez García");
            p.put("CLIENTE_DOCUMENTO", "12345678");
            p.put("CLIENTE_TELEFONO", "+57 311 234 5678");
            p.put("CLIENTE_EMAIL", "cliente@correo.com");
            p.put("VENDEDOR_NOMBRE", "Ana Martínez");
        } else if ("ORDEN_SERVICIO".equalsIgnoreCase(tipoReporte)) {
            p.put("EMPRESA_NOMBRE", "Empresa de Ejemplo S.A.S.");
            p.put("EMPRESA_NIT", "900.123.456-7");
            p.put("EMPRESA_TELEFONO", "+57 300 123 4567");
            p.put("ORDEN_ID", "OS-DEMO-001");
            p.put("ORDEN_ESTADO", "EN_PROCESO");
            p.put("ORDEN_FECHA_INGRESO", "01/01/2026 08:00");
            p.put("ORDEN_FECHA_SALIDA", "Pendiente");
            p.put("ORDEN_GARANTIA", "30 días");
            p.put("CLIENTE_NOMBRE", "Juan Pérez García");
            p.put("CLIENTE_DOCUMENTO", "12345678");
            p.put("CLIENTE_TELEFONO", "+57 311 234 5678");
            p.put("CLIENTE_EMAIL", "cliente@correo.com");
            p.put("EQUIPO_TIPO", "Nevera");
            p.put("EQUIPO_MARCA", "Samsung");
            p.put("EQUIPO_MODELO", "RFG29PHDBSR");
            p.put("EQUIPO_SERIE", "SN-789456");
            p.put("TIPO_SERVICIO", "Reparación preventiva");
            p.put("DESCRIPCION_PROBLEMA", "El equipo no enfría correctamente y hace ruido excesivo.");
            p.put("DIAGNOSTICO", "Compresor con desgaste prematuro. Gas refrigerante bajo.");
            p.put("SOLUCION", "Reemplazo de compresor y recarga de gas refrigerante R134a.");
            p.put("PARTES_CAMBIADAS", "Compresor DC Inverter");
            p.put("COSTO_SERVICIO", "$ 280.000,00");
            p.put("TOTAL_COSTO", "$ 280.000,00");
            p.put("TECNICO_NOMBRE", "Carlos Rodríguez");
            p.put("RECEPCIONISTA_NOMBRE", "María López");
            p.put("OBSERVACIONES", "Garantía de 30 días sobre el servicio prestado.");
        } else {
            // Parámetros genéricos para otros tipos
            p.put("EMPRESA_NOMBRE", "Empresa de Ejemplo");
            p.put("TITULO", "REPORTE DE EJEMPLO");
            p.put("FECHA", "01/01/2026");
        }
        return p;
    }

    private List<VentaDetalleDto> buildSampleDetalles() {
        VentaDetalleDto item1 = new VentaDetalleDto(
                "P001", "Servicio de mantenimiento preventivo", 1,
                new BigDecimal("150000"), new BigDecimal("150000"));
        VentaDetalleDto item2 = new VentaDetalleDto(
                "P002", "Repuesto ventilador interno", 2,
                new BigDecimal("75000"), new BigDecimal("150000"));
        VentaDetalleDto item3 = new VentaDetalleDto(
                "P003", "Mano de obra técnica", 1,
                new BigDecimal("50000"), new BigDecimal("50000"));
        return List.of(item1, item2, item3);
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private String nvl(String s) {
        return s != null ? s : "";
    }

    private String trim(String s) {
        return s != null ? s.trim() : "";
    }

    private String safeGet(Map<String, String> map, String key, String defaultValue) {
        if (map == null) return defaultValue;
        String val = map.get(key);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }

    private String formatMonto(BigDecimal monto) {
        if (monto == null) return "$ 0.00";
        return "$ " + monto.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
