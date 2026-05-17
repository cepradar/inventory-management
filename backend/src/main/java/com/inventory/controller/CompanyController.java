package com.inventory.controller;

import com.inventory.dto.CompanyDto;
import com.inventory.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Obtener información de la empresa (principal)
     */
    @GetMapping("/info")
    public ResponseEntity<CompanyDto> getCompanyInfo() {
        return companyService.getCompanyInfo()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Obtener empresa por NIT
     */
    @GetMapping("/nit/{nit}")
    public ResponseEntity<CompanyDto> getCompanyByNit(@PathVariable String nit) {
        return companyService.getCompanyByNit(nit)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crear nueva empresa
     */
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyDto companyDto) {
        try {
            CompanyDto created = companyService.createCompany(companyDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualizar información de la empresa
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyDto> updateCompany(
            @PathVariable Long id,
            @RequestBody CompanyDto companyDto) {
        try {
            CompanyDto updated = companyService.updateCompany(id, companyDto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualizar logo de la empresa
     */
    @PostMapping("/{id}/logo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateCompanyLogo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo no puede estar vacío");
            }
            companyService.updateCompanyLogo(id, file);
            return ResponseEntity.ok("Logo actualizado exitosamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el logo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualizar segundo logo de la empresa
     */
    @PostMapping("/{id}/logo2")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateCompanyLogo2(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo no puede estar vacío");
            }
            companyService.updateCompanyLogo2(id, file);
            return ResponseEntity.ok("Logo2 actualizado exitosamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el logo2: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener logo de la empresa
     */
    @GetMapping("/{id}/logo")
    public ResponseEntity<byte[]> getCompanyLogo(@PathVariable Long id) {
        return companyService.getCompanyLogo(id)
                .map(logo -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(logo))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

        /**
         * Obtener segundo logo de la empresa
         */
        @GetMapping("/{id}/logo2")
        public ResponseEntity<byte[]> getCompanyLogo2(@PathVariable Long id) {
        return companyService.getCompanyLogo2(id)
            .map(logo -> ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(logo))
            .orElseGet(() -> ResponseEntity.notFound().build());
        }

    /**
     * Obtener todas las empresas
     */
    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CompanyDto>> getAllCompanies() {
        List<CompanyDto> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    /**
     * Eliminar empresa
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCompany(@PathVariable Long id) {
        try {
            companyService.deleteCompany(id);
            return ResponseEntity.ok("Empresa eliminada exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
