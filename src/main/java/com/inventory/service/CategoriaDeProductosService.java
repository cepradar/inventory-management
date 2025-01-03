package com.inventory.service;

import com.inventory.dto.CategoriasDeProductoDto;
import com.inventory.model.CategoriasDeProducto;
import com.inventory.repository.CategoriasDeProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaDeProductosService {

    @Autowired
    private CategoriasDeProductoRepository categoryRepository;

    public CategoriasDeProducto crearCategoria(CategoriasDeProductoDto categoriaDto) {
        // Verificamos si la categoría ya existe por nombre (usando el DTO para obtener
        // el nombre)
        Optional<CategoriasDeProducto> categoryExists = categoryRepository.findByName(categoriaDto.getName());
        if (categoryExists.isPresent()) {
            throw new RuntimeException("La categoría ya existe");
        }
        CategoriasDeProducto categoria = CategoriasDeProductoDto.toCategoria(categoriaDto);
        return categoryRepository.save(categoria);
    }

    public List<CategoriasDeProductoDto> obtenerCategorias() {
        // Obtenemos todos los categorias desde la base de datos
        List<CategoriasDeProducto> categorias = categoryRepository.findAll();

        // Convertimos la lista de productos a una lista de ProductDto
        return categorias.stream()
                .map(CategoriasDeProductoDto::new) // Convierte cada producto en ProductDTO
                .collect(Collectors.toList());
    }

    public Optional<CategoriasDeProductoDto> obtenerCategoriaPorNombre(String nombre) {
        // Buscamos la categoria por ID
        Optional<CategoriasDeProducto> categoria = categoryRepository.findByName(nombre);

        // Si se encuentra, lo convertimos a ProductDto y lo devolvemos
        return categoria.map(CategoriasDeProductoDto::new);
    }

    public CategoriasDeProducto actualizarCategoria(CategoriasDeProductoDto category) {
        // Convertimos el ProductDto a Producto
        CategoriasDeProducto categoria = CategoriasDeProductoDto.toCategoria(category);

        // Guardamos el Producto actualizado
        return categoryRepository.save(categoria);
    }

    public void eliminarCategoria(CategoriasDeProductoDto categoriaDto) {
        // Obtenemos el id del ProductDto
        // Buscamos la categoría por nombre (o algún campo único)
        Optional<CategoriasDeProducto> categoriaOptional = categoryRepository.findByName(categoriaDto.getName());

        // Si la categoría existe, la eliminamos
        if (categoriaOptional.isPresent()) {
            categoryRepository.delete(categoriaOptional.get());
        } else {
            // Si no se encuentra la categoría, lanzamos una excepción o manejamos el error
            throw new IllegalArgumentException("Categoría no encontrada");
        }
    }
}
