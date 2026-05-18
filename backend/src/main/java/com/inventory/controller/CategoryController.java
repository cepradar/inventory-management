package com.inventory.controller;

import com.inventory.dto.CategoryProductDto;
import com.inventory.model.CategoryProduct;
import com.inventory.repository.CategoryProductRepository;
import com.inventory.service.CategoriaDeProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoriaDeProductosService categoryService;

    @Autowired
    private CategoryProductRepository categoryProductRepository;

    // Obtener todas las categorías
    @GetMapping("/listarCategoria")
    public ResponseEntity<List<CategoryProductDto>> obtenerCategorias() {
        return ResponseEntity.ok(categoryService.obtenerCategorias());
    }

    // Crear una nueva categoría
    @PostMapping("/crearCategoria")
    public ResponseEntity<CategoryProductDto> crearCategoria(@RequestBody CategoryProductDto category) {
        try {
             // Convertimos el DTO a la entidad correspondiente para la creación
             CategoryProduct categoriaCreada = categoryService.crearCategoria(category);
 
             // Convertimos la entidad creada de nuevo a un DTO para la respuesta
             CategoryProductDto categoriaRespuesta = new CategoryProductDto(categoriaCreada);
             return new ResponseEntity<>(categoriaRespuesta, HttpStatus.CREATED);
           // CategoriasDeProducto createdCategory = CategoriasDeProductoDto.toCategoria(category);
           // return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // En caso de error, como categoría duplicada
        }
    }

     // Editar una categoría
     @PutMapping("/editarCategoria/{id}")
     public ResponseEntity<CategoryProductDto> editarCategoria(@PathVariable String id, @RequestBody CategoryProductDto categoriaDto) {
         // Buscar la categoría por ID
         Optional<CategoryProductDto> categoriaExistente = categoryService.obtenerCategoriaPorNombre(categoriaDto.getName());
 
         if (categoriaExistente.isPresent()) {
             CategoryProductDto categoriaActualizada = categoriaExistente.get();
             categoriaActualizada.setName(categoriaDto.getName());
             categoriaActualizada.setDescription(categoriaDto.getDescription());
 
             // Actualizamos la categoría
             CategoryProduct categoriaGuardada = categoryService.actualizarCategoria(categoriaActualizada);
 
             // Convertimos la categoría actualizada a DTO antes de enviarla
             CategoryProductDto categoriaRespuesta = new CategoryProductDto(categoriaGuardada);
             return new ResponseEntity<>(categoriaRespuesta, HttpStatus.OK);
         }
 
         // Si no se encuentra la categoría, devolvemos un NOT_FOUND
         return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
     }

    // Eliminar una categoría (legacy — usa body)
    @DeleteMapping("/eliminarCategoria/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable String id, @RequestBody CategoryProductDto categoriaDto) {
        try {
            categoryService.eliminarCategoria(categoriaDto);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ── Endpoints estándar REST ──────────────────────────────────────────────

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryProductDto> actualizar(@PathVariable String id, @RequestBody CategoryProductDto dto) {
        return categoryProductRepository.findById(id).map(existing -> {
            if (dto.getName() != null) existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            CategoryProduct saved = categoryProductRepository.save(existing);
            return ResponseEntity.ok(new CategoryProductDto(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (!categoryProductRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoryProductRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
