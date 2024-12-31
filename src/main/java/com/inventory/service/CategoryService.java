package com.inventory.service;

import com.inventory.model.Category;
import com.inventory.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category crearCategoria(String name) {
        Optional<Category> categoryExists = categoryRepository.findByName(name);
        if (categoryExists.isPresent()) {
            throw new RuntimeException("La categoría ya existe");
        }
        Category categoria = new Category();
        categoria.setName(name);
        return categoryRepository.save(categoria);
    }

    public List<Category> obtenerCategorias() {
        return categoryRepository.findAll();
    }

    public Optional<Category> obtenerCategoriaPorId(Long id) {
        return categoryRepository.findById(id);
    }

    public Category actualizarCategoria(Category category) {
        return categoryRepository.save(category);
    }

    public void eliminarCategoria(Long id) {
        categoryRepository.deleteById(id);
    }
}
