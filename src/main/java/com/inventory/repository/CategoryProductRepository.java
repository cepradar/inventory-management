package com.inventory.repository;

import com.inventory.model.CategoryProduct;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryProductRepository extends JpaRepository<CategoryProduct, String> {
    Optional<CategoryProduct> findByName(String name);
    Optional<CategoryProduct> findById(String id);
}