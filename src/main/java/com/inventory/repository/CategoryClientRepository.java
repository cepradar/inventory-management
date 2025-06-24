package com.inventory.repository;

import com.inventory.model.CategoryClient;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryClientRepository extends JpaRepository<CategoryClient, String> {
    Optional<CategoryClient> findByName(String name);
    Optional<CategoryClient> findById(String id);
}
