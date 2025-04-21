package com.inventory.controller;

import com.inventory.dto.CategoryProductDto;
import com.inventory.dto.ProductDto;
import com.inventory.model.Product;
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
    public ResponseEntity<ProductDto> agregarProducto(@RequestBody ProductDto productDto) {
        try {
            // Convertimos el DTO a la entidad correspondiente para la creación
            Product productoCreado = productService.agregarProducto(productDto);
            // Convertimos la entidad creada de nuevo a un DTO para la respuesta
            ProductDto productoRespuesta = new ProductDto(productoCreado);
            return new ResponseEntity<>(productoRespuesta, HttpStatus.CREATED);
            // CategoriasDeProducto createdCategory = CategoriasDeProductoDto.toCategoria(category);
            // return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // En caso de error, como categoría duplicada
        }
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
    public ResponseEntity<List<ProductDto>> obtenerProductos() {
        List<ProductDto> productosDTO = productService.obtenerProductos();
        return ResponseEntity.ok(productosDTO);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ProductDto> actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductDto productosDto) {
        // Verificar si el producto existe
        Optional<ProductDto> productoExistente = productService.obtenerProductoPorId(productosDto.getId());
        if (productoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Producto no encontrado
        }

        // Actualizar los campos del producto
        Product producto = ProductDto.toProducto(productosDto);

        // Manejar la categoría
        if (productosDto.getName() != null) {
            CategoryProductDto categoria = categoryService.obtenerCategoriaPorNombre(productosDto.getName())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategory(CategoryProductDto.toCategoria(categoria));
        }

        Product productoActualizado = productService.actualizarProducto(productosDto);

        // Convertir el producto actualizado a DTO antes de devolverlo
        ProductDto productoDto = new ProductDto(productoActualizado);
        return ResponseEntity.ok(productoDto);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id, ProductDto productosDto) {
        // Verificar si el producto existe
        Optional<ProductDto> productoExistente = productService.obtenerProductoPorId(productosDto.getId());
        if (productoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Producto no encontrado
        }

        // Eliminar el producto
        productService.eliminarProducto(productosDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
