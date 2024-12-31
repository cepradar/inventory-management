package com.inventory.controller;

import com.inventory.model.Category;
import com.inventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Obtener todas las categorías
    @GetMapping
    public ResponseEntity<List<Category>> obtenerCategorias() {
        List<Category> categories = categoryService.obtenerCategorias();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Crear una nueva categoría
    @PostMapping
    public ResponseEntity<Category> crearCategoria(@RequestBody Category category) {
        try {
            Category createdCategory = categoryService.crearCategoria(category.getName());
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // En caso de error, como categoría duplicada
        }
    }

    // Editar una categoría
    @PutMapping("/{id}")
    public ResponseEntity<Category> editarCategoria(@PathVariable Long id, @RequestBody Category category) {
        Optional<Category> existingCategory = categoryService.obtenerCategoriaPorId(id);
        
        if (existingCategory.isPresent()) {
            Category updatedCategory = existingCategory.get();
            updatedCategory.setName(category.getName());
            updatedCategory.setDescription(category.getDescription());
            
            Category savedCategory = categoryService.actualizarCategoria(updatedCategory);
            return new ResponseEntity<>(savedCategory, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Si no se encuentra la categoría
    }

    // Eliminar una categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        Optional<Category> category = categoryService.obtenerCategoriaPorId(id);
        
        if (category.isPresent()) {
            categoryService.eliminarCategoria(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Si la eliminación es exitosa
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si no se encuentra la categoría
    }
}
