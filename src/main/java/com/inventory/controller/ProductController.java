package com.inventory.controller;

import com.inventory.dto.CategoriasDeProductoDto;
import com.inventory.dto.ProductosDto;
import com.inventory.model.CategoriasDeProducto;
import com.inventory.model.Productos;
import com.inventory.service.CategoriaDeProductosService;
import com.inventory.service.ProductoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductoService productService;

    @Autowired
    private CategoriaDeProductosService categoryService;

    @PostMapping("/agregar")
    public ResponseEntity<Productos> agregarProducto(@RequestBody ProductosDto productDto) {
        // Verificar si el ID de la categoría está presente
        if (productDto.getCategoryId() == null) {
            throw new RuntimeException("Se debe seleccionar una categoría");
        }

        // Buscar la categoría por el ID
        CategoriasDeProductoDto categoria = categoryService.obtenerCategoriaPorNombre(productDto.getName())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Guardar el nuevo producto
        Productos nuevoProducto = productService.agregarProducto(productDto);

        // Retornar el nuevo producto con un código de estado HTTP 201 (CREADO)
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    /*
     * @PostMapping("/categoria")
     * public ResponseEntity<Category> agregarCategoria(@RequestBody String nombre)
     * {
     * Category nuevaCategoria = categoryService.crearCategoria(nombre);
     * return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
     * }
     * 
     * 
     * @GetMapping("/categorias")
     * public ResponseEntity<List<CategoriasDeProducto>> obtenerCategorias() {
     * List<CategoriasDeProducto> categorias = categoryService.obtenerCategorias();
     * return ResponseEntity.ok(categorias);
     * }
     */

    @GetMapping("/listar")
    public ResponseEntity<List<ProductosDto>> obtenerProductos() {
        List<ProductosDto> productosDTO = productService.obtenerProductos();
        return ResponseEntity.ok(productosDTO);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ProductosDto> actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductosDto productosDto) {
        // Verificar si el producto existe
        Optional<ProductosDto> productoExistente = productService.obtenerProductoPorId(productosDto.getId());
        if (productoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Producto no encontrado
        }

        // Actualizar los campos del producto
        Productos producto = ProductosDto.toProducto(productosDto);

        // Manejar la categoría
        if (productosDto.getName() != null) {
            CategoriasDeProductoDto categoria = categoryService.obtenerCategoriaPorNombre(productosDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategory(CategoriasDeProductoDto.toCategoria(categoria));
        }

        Productos productoActualizado = productService.actualizarProducto(productosDto);

        // Convertir el producto actualizado a DTO antes de devolverlo
        ProductosDto productoDto = new ProductosDto(productoActualizado);
        return ResponseEntity.ok(productoDto);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id, ProductosDto productosDto) {
        // Verificar si el producto existe
        Optional<ProductosDto> productoExistente = productService.obtenerProductoPorId(productosDto.getId());
        if (productoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Producto no encontrado
        }

        // Eliminar el producto
        productService.eliminarProducto(productosDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
