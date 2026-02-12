package com.inventory.repository;

import com.inventory.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByNit(String nit);
    
    Optional<Company> findByRazonSocial(String razonSocial);
    
    @Query("SELECT c FROM Company c ORDER BY c.id DESC LIMIT 1")
    Optional<Company> findFirstCompany();
}
