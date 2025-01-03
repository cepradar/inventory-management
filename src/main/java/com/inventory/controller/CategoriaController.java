package com.inventory.controller;

import com.inventory.dto.CategoriasDeProductoDto;
import com.inventory.model.CategoriasDeProducto;
import com.inventory.service.CategoriaDeProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoriaController {

    @Autowired
    private CategoriaDeProductosService categoryService;

    // Obtener todas las categorías
    @GetMapping
    public ResponseEntity<List<CategoriasDeProductoDto>> obtenerCategorias() {
        return ResponseEntity.ok(categoryService.obtenerCategorias());
    }

    // Crear una nueva categoría
    @PostMapping
    public ResponseEntity<CategoriasDeProductoDto> crearCategoria(@RequestBody CategoriasDeProductoDto category) {
        try {
             // Convertimos el DTO a la entidad correspondiente para la creación
             CategoriasDeProducto categoriaCreada = categoryService.crearCategoria(category);
 
             // Convertimos la entidad creada de nuevo a un DTO para la respuesta
             CategoriasDeProductoDto categoriaRespuesta = new CategoriasDeProductoDto(categoriaCreada);
             return new ResponseEntity<>(categoriaRespuesta, HttpStatus.CREATED);
           // CategoriasDeProducto createdCategory = CategoriasDeProductoDto.toCategoria(category);
           // return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // En caso de error, como categoría duplicada
        }
    }

     // Editar una categoría
     @PutMapping("/{id}")
     public ResponseEntity<CategoriasDeProductoDto> editarCategoria(@PathVariable String id, @RequestBody CategoriasDeProductoDto categoriaDto) {
         // Buscar la categoría por ID
         Optional<CategoriasDeProductoDto> categoriaExistente = categoryService.obtenerCategoriaPorNombre(categoriaDto.getName());
 
         if (categoriaExistente.isPresent()) {
             CategoriasDeProductoDto categoriaActualizada = categoriaExistente.get();
             categoriaActualizada.setName(categoriaDto.getName());
             categoriaActualizada.setDescription(categoriaDto.getDescription());
 
             // Actualizamos la categoría
             CategoriasDeProducto categoriaGuardada = categoryService.actualizarCategoria(categoriaActualizada);
 
             // Convertimos la categoría actualizada a DTO antes de enviarla
             CategoriasDeProductoDto categoriaRespuesta = new CategoriasDeProductoDto(categoriaGuardada);
             return new ResponseEntity<>(categoriaRespuesta, HttpStatus.OK);
         }
 
         // Si no se encuentra la categoría, devolvemos un NOT_FOUND
         return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
     }

    // Eliminar una categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable String id, @RequestBody CategoriasDeProductoDto categoriaDto) {
        try {
            // Llamamos al servicio para eliminar la categoría
            categoryService.eliminarCategoria(categoriaDto);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Si la eliminación es exitosa
        } catch (IllegalArgumentException e) {
            // Si la categoría no se encuentra
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
}
