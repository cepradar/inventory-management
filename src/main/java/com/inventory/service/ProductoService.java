package com.inventory.service;

import com.inventory.dto.ProductosDto;
import com.inventory.model.Productos;
import com.inventory.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductosRepository productRepository;

    public Productos agregarProducto(ProductosDto productDto) {
        // Convertimos el ProductDto en Producto
        Productos producto = ProductosDto.toProducto(productDto);

        // Guardamos el Producto en la base de datos
        return productRepository.save(producto);
    }

     public List<ProductosDto> obtenerProductos() {
        // Obtenemos todos los productos desde la base de datos
        List<Productos> productos = productRepository.findAll();
    
        // Convertimos la lista de productos a una lista de ProductDto
        return productos.stream()
                        .map(ProductosDto::new) // Convierte cada producto en ProductDTO
                        .collect(Collectors.toList());
    }

    public Optional<ProductosDto> obtenerProductoPorId(Long id) {
        // Buscamos el producto por ID
        Optional<Productos> producto = productRepository.findById(id);
        
        // Si se encuentra, lo convertimos a ProductDto y lo devolvemos
        return producto.map(ProductosDto::new);
    }

    public Productos actualizarProducto(ProductosDto productDto) {
        // Convertimos el ProductDto a Producto
        Productos producto = ProductosDto.toProducto(productDto);

        // Guardamos el Producto actualizado
        return productRepository.save(producto);
    }

    public void eliminarProducto(ProductosDto productDto) {
        // Obtenemos el id del ProductDto
        Long id = productDto.getId();
        
        // Eliminamos el Producto por id
        productRepository.deleteById(id);
    }

}
