package com.inventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.model.CategoryClient;
import com.inventory.repository.CategoryClientRepository;

@RestController
@RequestMapping("/api/client-categories")
public class CategoryClientController {

    @Autowired
    private CategoryClientRepository categoryClientRepository;

    @GetMapping("/listar")
    public ResponseEntity<List<CategoryClient>> listarCategoriasCliente() {
        return ResponseEntity.ok(categoryClientRepository.findAll());
    }
}
