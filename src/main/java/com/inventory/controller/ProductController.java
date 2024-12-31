package com.inventory.controller;

import com.inventory.dto.ProductDto;
import com.inventory.dto.ProductUpdateDto;
import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;

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
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/agregar")
    public ResponseEntity<Product> agregarProducto(@RequestBody Product product) {
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new RuntimeException("Se debe seleccionar una categoría");
        }
        Category categoria = categoryService.obtenerCategoriaPorId(product.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        product.setCategory(categoria);
        Product nuevoProducto = productService.agregarProducto(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    /*
     * @PostMapping("/categoria")
     * public ResponseEntity<Category> agregarCategoria(@RequestBody String nombre)
     * {
     * Category nuevaCategoria = categoryService.crearCategoria(nombre);
     * return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
     * }
     */

    @GetMapping("/categorias")
    public ResponseEntity<List<Category>> obtenerCategorias() {
        List<Category> categorias = categoryService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ProductDto>> obtenerProductos() {
        List<ProductDto> productosDTO = productService.obtenerProductos();
        return ResponseEntity.ok(productosDTO);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ProductDto> actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductUpdateDto productUpdateDto) {
        // Verificar si el producto existe
        Optional<Product> productoExistente = productService.obtenerProductoPorId(id);
        if (productoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Producto no encontrado
        }

        // Actualizar los campos del producto
        Product producto = productoExistente.get();
        producto.setName(productUpdateDto.getName());
        producto.setDescription(productUpdateDto.getDescription());
        producto.setPrice(productUpdateDto.getPrice());
        producto.setQuantity(productUpdateDto.getQuantity());

        // Manejar la categoría
        if (productUpdateDto.getCategoryId() != null) {
            Category categoria = categoryService.obtenerCategoriaPorId(productUpdateDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategory(categoria);
        }

        Product productoActualizado = productService.actualizarProducto(producto);

        // Convertir el producto actualizado a DTO antes de devolverlo
        ProductDto productoDto = new ProductDto(productoActualizado);
        return ResponseEntity.ok(productoDto);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        // Verificar si el producto existe
        Optional<Product> productoExistente = productService.obtenerProductoPorId(id);
        if (productoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Producto no encontrado
        }

        // Eliminar el producto
        productService.eliminarProducto(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
