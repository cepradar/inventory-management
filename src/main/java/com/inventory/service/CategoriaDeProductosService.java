package com.inventory.service;

import com.inventory.dto.CategoryProductDto;
import com.inventory.model.CategoryProduct;
import com.inventory.repository.CategoryProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaDeProductosService {

    //Registro de logs
    private static final Logger logger = LoggerFactory.getLogger(CategoriaDeProductosService.class);

    @Autowired
    private CategoryProductRepository categoryRepository;

    public CategoryProduct crearCategoria(CategoryProductDto categoriaDto) {
        // Verificamos si la categoría ya existe por nombre (usando el DTO para obtener
        // el nombre)
        Optional<CategoryProduct> categoryExists = categoryRepository.findByName(categoriaDto.getName());
        if (categoryExists.isPresent()) {
            logger.info("La categoria ya existe");
            throw new RuntimeException("La categoría ya existe");
        }
        CategoryProduct categoria = CategoryProductDto.toCategoria(categoriaDto);
        return categoryRepository.save(categoria);
    }

    public List<CategoryProductDto> obtenerCategorias() {
        // Obtenemos todos los categorias desde la base de datos
        List<CategoryProduct> categorias = categoryRepository.findAll();

        // Convertimos la lista de productos a una lista de ProductDto
        return categorias.stream()
                .map(CategoryProductDto::new) // Convierte cada producto en ProductDTO
                .collect(Collectors.toList());
    }

    public Optional<CategoryProductDto> obtenerCategoriaPorNombre(String nombre) {
        // Buscamos la categoria por ID
        Optional<CategoryProduct> categoria = categoryRepository.findByName(nombre);

        // Si se encuentra, lo convertimos a ProductDto y lo devolvemos
        return categoria.map(CategoryProductDto::new);
    }

    public CategoryProduct actualizarCategoria(CategoryProductDto category) {
        // Convertimos el ProductDto a Producto
        CategoryProduct categoria = CategoryProductDto.toCategoria(category);

        // Guardamos el Producto actualizado
        return categoryRepository.save(categoria);
    }

    public void eliminarCategoria(CategoryProductDto categoriaDto) {
        // Obtenemos el id del ProductDto
        // Buscamos la categoría por nombre (o algún campo único)
        Optional<CategoryProduct> categoriaOptional = categoryRepository.findByName(categoriaDto.getName());

        // Si la categoría existe, la eliminamos
        if (categoriaOptional.isPresent()) {
            categoryRepository.delete(categoriaOptional.get());
        } else {
            // Si no se encuentra la categoría, lanzamos una excepción o manejamos el error
            throw new IllegalArgumentException("Categoría no encontrada");
        }
    }

    public Optional<CategoryProduct> obtenerCategoriaPorId(String id) {
        return categoryRepository.findById(id);
    }

}
