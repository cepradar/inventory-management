package com.inventory.repository;

import com.inventory.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Rol, String> {
    Rol findByName(String name);
}
