package com.inventory.service;

import com.inventory.dto.ProductDto;
import com.inventory.model.CategoriaElectrodomestico;
import com.inventory.model.CategoryProduct;
import com.inventory.model.Product;
import com.inventory.repository.CategoriaElectrodomesticoRepository;
import com.inventory.repository.CategoryProductRepository;
import com.inventory.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private CategoryProductRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoriaElectrodomesticoRepository categoriaElectrodomesticoRepository;


    public Product agregarProducto(ProductDto productDto) {
        String productId = productDto.getId();
        if (productId == null || productId.trim().isEmpty()) {
            throw new RuntimeException("El codigo de producto es obligatorio");
        }

        productId = productId.trim();
        productDto.setId(productId);
        if (productRepository.existsById(productId)) {
            throw new RuntimeException("El codigo de producto ya existe");
        }

        // 🔥 Validar que la categoría existe
        String categoryId = Objects.requireNonNull(productDto.getCategoryId(), "categoryId");
        CategoryProduct categoria = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("La categoría con id " + categoryId + " no existe"));

        // 🔧 Convertir DTO a entidad y setear categoría real
        Product producto = ProductDto.toProducto(productDto);
        producto.setCategory(categoria); // Esta categoría sí existe en la BD
        if (productDto.getCategoriaElectrodomesticoId() != null) {
                Long categoriaElectrodomesticoId = Objects.requireNonNull(productDto.getCategoriaElectrodomesticoId(), "categoriaElectrodomesticoId");
                CategoriaElectrodomestico categoriaElectrodomestico = categoriaElectrodomesticoRepository
                    .findById(categoriaElectrodomesticoId)
                    .orElseThrow(() -> new RuntimeException(
                            "La categoría de electrodoméstico con id " + productDto.getCategoriaElectrodomesticoId() + " no existe"));
            producto.setCategoriaElectrodomestico(categoriaElectrodomestico);
        } else {
            producto.setCategoriaElectrodomestico(null);
        }

        return productRepository.save(producto);
    }

    public List<ProductDto> obtenerProductos() {
        // Obtenemos todos los productos desde la base de datos
        List<Product> productos = productRepository.findAll();

        // Convertimos la lista de productos a una lista de ProductDto
        return productos.stream()
                .map(ProductDto::new) // Convierte cada producto en ProductDTO
                .collect(Collectors.toList());
    }

    public Optional<ProductDto> obtenerProductoPorId(String id) {
        // Buscamos el producto por ID
        Optional<Product> producto = productRepository.findById(Objects.requireNonNull(id, "id"));

        // Si se encuentra, lo convertimos a ProductDto y lo devolvemos
        return producto.map(ProductDto::new);
    }

    public Product actualizarProducto(ProductDto productDto) {
        String productoId = Objects.requireNonNull(productDto.getId(), "productId");
        Product producto = productRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("El producto con id " + productDto.getId() + " no existe"));

        producto.setName(productDto.getName());
        producto.setDescription(productDto.getDescription());
        producto.setPrice(productDto.getPrice());
        producto.setQuantity(productDto.getQuantity());
        if (productDto.getActivo() != null) {
            producto.setActivo(productDto.getActivo());
        }

        if (productDto.getCategoryId() != null) {
                String categoryId = Objects.requireNonNull(productDto.getCategoryId(), "categoryId");
                CategoryProduct categoria = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("La categoría con id " + productDto.getCategoryId() + " no existe"));
            producto.setCategory(categoria);
        }

        if (productDto.getCategoriaElectrodomesticoId() != null) {
                Long categoriaElectrodomesticoId = Objects.requireNonNull(productDto.getCategoriaElectrodomesticoId(), "categoriaElectrodomesticoId");
                CategoriaElectrodomestico categoriaElectrodomestico = categoriaElectrodomesticoRepository
                    .findById(categoriaElectrodomesticoId)
                    .orElseThrow(() -> new RuntimeException(
                            "La categoría de electrodoméstico con id " + productDto.getCategoriaElectrodomesticoId() + " no existe"));
            producto.setCategoriaElectrodomestico(categoriaElectrodomestico);
        } else {
            producto.setCategoriaElectrodomestico(null);
        }

        return productRepository.save(producto);
    }

    public void eliminarProducto(ProductDto productDto) {
        String id = productDto != null ? productDto.getId() : null;
        eliminarProductoPorId(id);
    }

    public void eliminarProductoPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new RuntimeException("El id del producto es obligatorio");
        }

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("El producto con id " + id + " no existe");
        }

        productRepository.deleteById(id);
    }

}