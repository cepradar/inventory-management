package com.inventory;

import com.inventory.dto.MovimientoProductoDto;
import com.inventory.dto.VentaDto;
import com.inventory.model.*;
import com.inventory.repository.*;
import com.inventory.service.AuditoriaService;
import com.inventory.service.VentasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AuditoriaVentasIntegrationTest {

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private VentasService ventasService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovimientoProductoRepository movimientoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    private Product testProduct;
    private User testUser;

    @BeforeEach
    public void setUp() {
        // Crear usuario de prueba si no existe
        testUser = userRepository.findById("testuser").orElse(null);
        if (testUser == null) {
            testUser = new User();
            testUser.setUsername("testuser");
            testUser.setEmail("test@example.com");
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setPassword("hashed_password");
            testUser = userRepository.save(testUser);
        }

        // Crear producto de prueba
        testProduct = new Product("Test Product", 100.0, 50, null);
        testProduct.setId("TEST001");
        testProduct = productRepository.save(testProduct);
    }

    @Test
    public void testRegistrarMovimiento() {
        // Arrange
        String productId = testProduct.getId();
        Integer cantidad = 10;
        String tipo = "INGRESO";
        String descripcion = "Stock inicial";
        String usuarioUsername = testUser.getUsername();
        String referencia = "INIT-001";

        // Act
        MovimientoProductoDto resultado = auditoriaService.registrarMovimiento(
                productId, cantidad, tipo, descripcion, usuarioUsername, referencia
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(cantidad, resultado.getCantidad());
        assertEquals(tipo, resultado.getTipo());
        assertEquals(descripcion, resultado.getDescripcion());
        assertEquals(referencia, resultado.getReferencia());
        assertEquals(usuarioUsername, resultado.getUsuarioUsername());
    }

    @Test
    public void testObtenerMovimientosProducto() {
        // Arrange
        auditoriaService.registrarMovimiento(testProduct.getId(), 10, "INGRESO", 
                "Prueba", testUser.getUsername(), "REF-001");

        // Act
        List<MovimientoProductoDto> movimientos = auditoriaService
                .obtenerMovimientosProducto(testProduct.getId());

        // Assert
        assertNotNull(movimientos);
        assertTrue(movimientos.size() > 0);
        assertEquals(testProduct.getId(), String.valueOf(movimientos.get(0).getProductId()));
    }

    @Test
    public void testRegistrarVentaReduceInventario() {
        // Arrange
        int cantidadInicial = testProduct.getQuantity();
        int cantidadVenta = 5;
        BigDecimal precio = BigDecimal.valueOf(150.0);

        // Act
        VentaDto ventaDto = ventasService.registrarVenta(
                testProduct.getId(),
                cantidadVenta,
                precio,
                "Cliente Test",
                "123456789",
                "cliente@test.com",
                testUser.getUsername(),
                "Venta de prueba"
        );

        // Assert
        assertNotNull(ventaDto);
        assertEquals(cantidadVenta, ventaDto.getCantidad());
        assertEquals(precio, ventaDto.getPrecioUnitario());

        // Verificar que el inventario fue reducido
        Product productoActualizado = productRepository.findById(testProduct.getId()).orElse(null);
        assertNotNull(productoActualizado);
        assertEquals(cantidadInicial - cantidadVenta, productoActualizado.getQuantity());
    }

    @Test
    public void testRegistrarVentaCreaMovimientoSalida() {
        // Arrange
        int cantidadVenta = 3;
        BigDecimal precio = BigDecimal.valueOf(100.0);

        // Act
        VentaDto ventaDto = ventasService.registrarVenta(
                testProduct.getId(),
                cantidadVenta,
                precio,
                "Cliente Test 2",
                null,
                null,
                testUser.getUsername(),
                null
        );

        // Assert
        assertNotNull(ventaDto);

        // Verificar que se creó un movimiento SALIDA
        List<MovimientoProducto> movimientos = movimientoRepository
                .findMovimientosByProductAndTipo(testProduct.getId(), "SALIDA");
        
        assertTrue(movimientos.size() > 0, "Debe existir al menos un movimiento SALIDA");
        
        MovimientoProducto ultimoMovimiento = movimientos.get(0);
        assertEquals("SALIDA", ultimoMovimiento.getTipo());
        assertEquals(cantidadVenta, ultimoMovimiento.getCantidad());
    }

    @Test
    public void testObtenerVentasProducto() {
        // Arrange
        ventasService.registrarVenta(
                testProduct.getId(), 2, BigDecimal.valueOf(100.0),
                "Cliente A", null, null, testUser.getUsername(), null
        );
        ventasService.registrarVenta(
                testProduct.getId(), 3, BigDecimal.valueOf(100.0),
                "Cliente B", null, null, testUser.getUsername(), null
        );

        // Act
        List<VentaDto> ventas = ventasService.obtenerVentasProducto(testProduct.getId());

        // Assert
        assertNotNull(ventas);
        assertTrue(ventas.size() >= 2);
    }

    @Test
    public void testObtenerTotalVentasEnRango() {
        // Arrange
        ventasService.registrarVenta(
                testProduct.getId(), 5, BigDecimal.valueOf(100.0),
                "Cliente", null, null, testUser.getUsername(), null
        );

        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.LocalDateTime manana = ahora.plusDays(1);

        // Act
        BigDecimal total = ventasService.obtenerTotalVentasEnRango(ahora.minusDays(1), manana);

        // Assert
        assertNotNull(total);
        assertTrue(total.compareTo(BigDecimal.ZERO) > 0, "El total debe ser mayor a 0");
    }

    @Test
    public void testVentaConCantidadInsuficiente() {
        // Arrange
        int cantidadDisponible = testProduct.getQuantity();
        int cantidadVenta = cantidadDisponible + 100;

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            ventasService.registrarVenta(
                    testProduct.getId(),
                    cantidadVenta,
                    BigDecimal.valueOf(100.0),
                    "Cliente",
                    null,
                    null,
                    testUser.getUsername(),
                    null
            );
        }, "Debe lanzar excepción si cantidad es insuficiente");
    }

    @Test
    public void testObtenerMovimientosPorTipo() {
        // Arrange
        auditoriaService.registrarMovimiento(testProduct.getId(), 10, "INGRESO", 
                "Prueba", testUser.getUsername(), "REF-001");
        auditoriaService.registrarMovimiento(testProduct.getId(), 5, "INGRESO", 
                "Prueba 2", testUser.getUsername(), "REF-002");

        // Act
        List<MovimientoProductoDto> ingresos = auditoriaService.obtenerMovimientosPorTipo("INGRESO");

        // Assert
        assertNotNull(ingresos);
        assertTrue(ingresos.size() >= 2);
        assertTrue(ingresos.stream().allMatch(m -> m.getTipo().equals("INGRESO")));
    }
}
