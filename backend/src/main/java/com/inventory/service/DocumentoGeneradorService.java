package com.inventory.service;

import com.inventory.dto.OrdenDeServicioDto;
import com.inventory.dto.VentaDetalleDto;
import com.inventory.dto.VentaDto;
import com.inventory.model.ReportTemplate;
import com.inventory.repository.ReportTemplateRepository;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio facade para generación de documentos (PDF) del sistema.
 *
 * Arquitectura de resolución de plantilla:
 * <ol>
 *   <li>Busca plantilla activa en BD por módulo + tipoDocumento</li>
 *   <li>Si no hay ninguna en BD, usa la plantilla del sistema (classpath /reports/)</li>
 * </ol>
 *
 * Los módulos (Ventas, Órdenes, etc.) solo deben llamar a este servicio.
 * Ningún módulo necesita conocer JasperReports directamente.
 */
@Service
public class DocumentoGeneradorService {

    private static final Logger log = LoggerFactory.getLogger(DocumentoGeneradorService.class);

    private final JasperReportService jasperService;
    private final ReportTemplateRepository templateRepository;
    private final ReportStorageService storageService;
    private final VentasService ventasService;
    private final OrdenDeServicioService ordenService;
    private final CompanyService companyService;

    public DocumentoGeneradorService(JasperReportService jasperService,
                                     ReportTemplateRepository templateRepository,
                                     ReportStorageService storageService,
                                     VentasService ventasService,
                                     OrdenDeServicioService ordenService,
                                     CompanyService companyService) {
        this.jasperService = jasperService;
        this.templateRepository = templateRepository;
        this.storageService = storageService;
        this.ventasService = ventasService;
        this.ordenService = ordenService;
        this.companyService = companyService;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // API PÚBLICA — llamada desde controllers
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Genera el PDF de factura para una venta.
     *
     * @param ventaId ID de la venta
     * @return bytes del PDF
     */
    public byte[] generarFactura(Long ventaId) throws Exception {
        VentaDto venta = ventasService.obtenerVentaPorId(ventaId);
        Map<String, String> empresa = buildEmpresaMap();

        JasperReport report = resolverPlantilla(
                ReportTemplate.ModuloReporte.VENTAS,
                ReportTemplate.TipoDocumento.FACTURA_VENTA);

        Map<String, Object> params = buildFacturaParams(venta, empresa);
        List<VentaDetalleDto> detalles = venta.getDetalles() != null
                ? venta.getDetalles()
                : Collections.emptyList();

        return jasperService.exportToPdf(report, params, new JRBeanCollectionDataSource(detalles));
    }

    /**
     * Genera el PDF de orden de servicio.
     *
     * @param ordenId ID de la orden de servicio
     * @return bytes del PDF
     */
    public byte[] generarOrdenServicio(String ordenId) throws Exception {
        OrdenDeServicioDto orden = ordenService.obtenerServicioPorId(ordenId);
        Map<String, String> empresa = buildEmpresaMap();

        JasperReport report = resolverPlantilla(
                ReportTemplate.ModuloReporte.ORDENES,
                ReportTemplate.TipoDocumento.ORDEN_SERVICIO);

        Map<String, Object> params = buildOrdenParams(orden, empresa);
        return jasperService.exportToPdf(report, params, new JREmptyDataSource(1));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // RESOLUCIÓN DE PLANTILLA
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Resuelve la plantilla Jasper a usar:
     * 1. Busca en BD la plantilla activa del módulo/tipoDocumento con JASPER compilado
     * 2. Si no, busca en BD con JRXML (compila al vuelo)
     * 3. Si no, usa la del classpath (sistema)
     */
    private JasperReport resolverPlantilla(ReportTemplate.ModuloReporte modulo,
                                            ReportTemplate.TipoDocumento tipoDocumento) throws Exception {

        Optional<ReportTemplate> opt = templateRepository
                .findByModuloAndTipoDocumentoAndActivoTrue(modulo, tipoDocumento);

        if (opt.isPresent()) {
            ReportTemplate tpl = opt.get();
            log.info("Usando plantilla BD: {} (modulo={}, tipo={})", tpl.getNombre(), modulo, tipoDocumento);

            // Preferir .jasper compilado (más rápido)
            if (tpl.getArchivoJasperNombre() != null) {
                try {
                    byte[] jasperBytes = storageService.readFile(tpl.getArchivoJasperNombre());
                    return (JasperReport) net.sf.jasperreports.engine.util.JRLoader
                            .loadObject(new java.io.ByteArrayInputStream(jasperBytes));
                } catch (Exception e) {
                    log.warn("No se pudo cargar .jasper de BD, intentando .jrxml: {}", e.getMessage());
                }
            }

            // Fallback a .jrxml
            if (tpl.getArchivoJrxmlNombre() != null) {
                try {
                    byte[] jrxmlBytes = storageService.readFile(tpl.getArchivoJrxmlNombre());
                    return jasperService.compileJrxml(jrxmlBytes);
                } catch (Exception e) {
                    log.warn("No se pudo compilar .jrxml de BD, usando plantilla del sistema: {}", e.getMessage());
                }
            }
        }

        // Plantilla del sistema (classpath)
        String templateName = tipoDocumento.getTemplateName();
        log.info("Usando plantilla del sistema: {}.jrxml (modulo={}, tipo={})", templateName, modulo, tipoDocumento);
        return jasperService.getSystemTemplate(templateName);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CONSTRUCCIÓN DE PARÁMETROS
    // ═══════════════════════════════════════════════════════════════════════════

    private Map<String, String> buildEmpresaMap() {
        Map<String, String> empresa = new HashMap<>();
        companyService.getCompanyInfo().ifPresent(c -> {
            empresa.put("nombre", nvl(c.getRazonSocial()));
            empresa.put("nit", nvl(c.getNit()));
            empresa.put("direccion", nvl(c.getDireccion()));
            empresa.put("telefono", nvl(c.getTelefono()));
            empresa.put("email", nvl(c.getCorreo()));
        });
        return empresa;
    }

    private Map<String, Object> buildFacturaParams(VentaDto venta, Map<String, String> empresa) {
        Map<String, Object> p = new HashMap<>();
        // Empresa
        p.put("EMPRESA_NOMBRE",    safeGet(empresa, "nombre", "Mi Empresa"));
        p.put("EMPRESA_NIT",       safeGet(empresa, "nit", ""));
        p.put("EMPRESA_DIRECCION", safeGet(empresa, "direccion", ""));
        p.put("EMPRESA_TELEFONO",  safeGet(empresa, "telefono", ""));
        p.put("EMPRESA_EMAIL",     safeGet(empresa, "email", ""));
        // Venta
        p.put("VENTA_ID",           venta.getId() != null ? venta.getId().toString() : "");
        p.put("VENTA_FECHA",        venta.getFecha() != null
                ? venta.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        p.put("VENTA_TOTAL",        formatMonto(venta.getTotalVenta()));
        p.put("VENTA_OBSERVACIONES", nvl(venta.getObservaciones()));
        // Cliente
        p.put("CLIENTE_NOMBRE",     nvl(venta.getNombreComprador()));
        p.put("CLIENTE_DOCUMENTO",  nvl(venta.getClienteId()));
        p.put("CLIENTE_TELEFONO",   nvl(venta.getTelefonoComprador()));
        p.put("CLIENTE_EMAIL",      nvl(venta.getEmailComprador()));
        // Vendedor
        p.put("VENDEDOR_NOMBRE",    nvl(venta.getUsuarioNombre()));
        return p;
    }

    private Map<String, Object> buildOrdenParams(OrdenDeServicioDto orden, Map<String, String> empresa) {
        Map<String, Object> p = new HashMap<>();
        // Empresa
        p.put("EMPRESA_NOMBRE",    safeGet(empresa, "nombre", "Mi Empresa"));
        p.put("EMPRESA_NIT",       safeGet(empresa, "nit", ""));
        p.put("EMPRESA_DIRECCION", safeGet(empresa, "direccion", ""));
        p.put("EMPRESA_TELEFONO",  safeGet(empresa, "telefono", ""));
        p.put("EMPRESA_EMAIL",     safeGet(empresa, "email", ""));
        // Orden
        p.put("ORDEN_ID",          nvl(orden.getId()));
        p.put("ORDEN_ESTADO",      nvl(orden.getEstado()));
        p.put("ORDEN_FECHA_INGRESO", orden.getFechaIngreso() != null
                ? orden.getFechaIngreso().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        p.put("ORDEN_FECHA_SALIDA", orden.getFechaSalida() != null
                ? orden.getFechaSalida().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "Pendiente");
        p.put("ORDEN_GARANTIA",     orden.getGarantiaServicio() != null
                ? orden.getGarantiaServicio() + " días" : "");
        // Cliente
        p.put("CLIENTE_NOMBRE",     (nvl(orden.getClienteNombre()) + " " + nvl(orden.getClienteApellido())).trim());
        p.put("CLIENTE_DOCUMENTO",  nvl(orden.getClienteId()));
        p.put("CLIENTE_TELEFONO",   nvl(orden.getClienteTelefono()));
        p.put("CLIENTE_EMAIL",      nvl(orden.getClienteEmail()));
        // Equipo
        p.put("EQUIPO_TIPO",        nvl(orden.getElectrodomesticoTipo()));
        p.put("EQUIPO_MARCA",       nvl(orden.getElectrodomesticoMarca()));
        p.put("EQUIPO_MODELO",      nvl(orden.getElectrodomesticoModelo()));
        p.put("EQUIPO_SERIE",       "");
        // Servicio
        p.put("TIPO_SERVICIO",         nvl(orden.getTipoServicio()));
        p.put("DESCRIPCION_PROBLEMA",  nvl(orden.getDescripcionProblema()));
        p.put("DIAGNOSTICO",           nvl(orden.getDiagnostico()));
        p.put("SOLUCION",              nvl(orden.getSolucion()));
        p.put("PARTES_CAMBIADAS",      nvl(orden.getPartesCambiadas()));
        p.put("COSTO_SERVICIO",        formatMonto(orden.getCostoServicio()));
        p.put("TOTAL_COSTO",           formatMonto(orden.getTotalCosto()));
        p.put("OBSERVACIONES",         nvl(orden.getObservaciones()));
        // Personal
        p.put("TECNICO_NOMBRE",        nvl(orden.getTecnicoAsignadoNombre()));
        p.put("RECEPCIONISTA_NOMBRE",  nvl(orden.getUsuarioNombre()));
        return p;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UTILIDADES INTERNAS
    // ═══════════════════════════════════════════════════════════════════════════

    private String nvl(String s) {
        return s != null ? s : "";
    }

    private String safeGet(Map<String, String> map, String key, String def) {
        if (map == null) return def;
        String v = map.get(key);
        return (v != null && !v.isBlank()) ? v : def;
    }

    private String formatMonto(java.math.BigDecimal monto) {
        if (monto == null) return "$ 0.00";
        return "$ " + monto.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
