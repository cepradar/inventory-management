package com.inventory.service;

import com.inventory.dto.ProductDto;
import com.inventory.model.CategoryProduct;
import com.inventory.model.Product;
import com.inventory.repository.CategoryProductRepository;
import com.inventory.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private CategoryProductRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public Product agregarProducto(ProductDto productDto) {
        if (productDto.getId() != null) {
            Optional<Product> productoExiste = productRepository.findById(productDto.getId());
            if (productoExiste.isPresent()) {
                throw new RuntimeException("El producto ya existe");
            }
        }
    
        // 🔥 Validar que la categoría existe
        String categoryId = productDto.getCategoryId();
        CategoryProduct categoria = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("La categoría con id " + categoryId + " no existe"));
    
        // 🔧 Convertir DTO a entidad y setear categoría real
        Product producto = ProductDto.toProducto(productDto);
        producto.setCategory(categoria); // Esta categoría sí existe en la BD
    
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
        Optional<Product> producto = productRepository.findById(id);
        
        // Si se encuentra, lo convertimos a ProductDto y lo devolvemos
        return producto.map(ProductDto::new);
    }

    public Product actualizarProducto(ProductDto productDto) {
        // Convertimos el ProductDto a Producto
        Product producto = ProductDto.toProducto(productDto);

        // Guardamos el Producto actualizado
        return productRepository.save(producto);
    }

    public void eliminarProducto(ProductDto productDto) {
        // Obtenemos el id del ProductDto
        String id = productDto.getId();
        
        // Eliminamos el Producto por id
        productRepository.deleteById(id);
    }

}