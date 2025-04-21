package com.inventory.repository;

import com.inventory.model.CategoryProduct;
import com.inventory.model.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(CategoryProduct category);
}