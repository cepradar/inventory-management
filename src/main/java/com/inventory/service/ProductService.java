package com.inventory.service;

import com.inventory.dto.ProductDto;
import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Product agregarProducto(Product product) {
        return productRepository.save(product);
    }

     public List<ProductDto> obtenerProductos() {
        List<Product> productos = productRepository.findAll();
        return productos.stream()
                        .map(ProductDto::new) // Convierte cada producto en ProductDTO
                        .collect(Collectors.toList());
    }

    public Optional<Product> obtenerProductoPorId(Long id) {
        return productRepository.findById(id);
    }

    public Product actualizarProducto(Product product) {
        return productRepository.save(product);
    }

    public void eliminarProducto(Long id) {
        productRepository.deleteById(id);
    }

}
