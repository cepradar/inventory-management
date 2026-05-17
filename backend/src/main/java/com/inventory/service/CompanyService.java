package com.inventory.service;

import com.inventory.dto.CompanyDto;
import com.inventory.model.Company;
import com.inventory.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    /**
     * Obtener información de la empresa (principal)
     */
    @Transactional(readOnly = true)
    public Optional<CompanyDto> getCompanyInfo() {
        return companyRepository.findFirstCompany()
                .map(CompanyDto::new);
    }

    /**
     * Obtener empresa por NIT
     */
    @Transactional(readOnly = true)
    public Optional<CompanyDto> getCompanyByNit(String nit) {
        return companyRepository.findByNit(nit)
                .map(CompanyDto::new);
    }

    /**
     * Obtener empresa por Razón Social
     */
    @Transactional(readOnly = true)
    public Optional<CompanyDto> getCompanyByRazonSocial(String razonSocial) {
        return companyRepository.findByRazonSocial(razonSocial)
                .map(CompanyDto::new);
    }

    /**
     * Crear nueva empresa
     */
    @Transactional
    public CompanyDto createCompany(CompanyDto companyDto) {
        // Validar que no exista una empresa con el mismo NIT
        if (companyRepository.findByNit(companyDto.getNit()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una empresa con el NIT: " + companyDto.getNit());
        }

        Company company = CompanyDto.toCompany(companyDto);
        company.setFechaCreacion(LocalDateTime.now());
        company.setFechaActualizacion(LocalDateTime.now());

        Company savedCompany = companyRepository.save(company);
        return new CompanyDto(savedCompany);
    }

    /**
     * Actualizar información de la empresa
     */
    @Transactional
    public CompanyDto updateCompany(Long id, CompanyDto companyDto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada con ID: " + id));

        // Validar NIT único si cambió
        if (!company.getNit().equals(companyDto.getNit())) {
            if (companyRepository.findByNit(companyDto.getNit()).isPresent()) {
                throw new IllegalArgumentException("Ya existe una empresa con el NIT: " + companyDto.getNit());
            }
        }

        company.setRazonSocial(companyDto.getRazonSocial());
        company.setNit(companyDto.getNit());
        company.setDireccion(companyDto.getDireccion());
        company.setCiudad(companyDto.getCiudad());
        company.setDepartamento(companyDto.getDepartamento());
        company.setCodigoPostal(companyDto.getCodigoPostal());
        company.setTelefono(companyDto.getTelefono());
        company.setCorreo(companyDto.getCorreo());
        company.setSitioWeb(companyDto.getSitioWeb());
        company.setRepresentanteLegal(companyDto.getRepresentanteLegal());
        company.setNumeroRegimen(companyDto.getNumeroRegimen());
        company.setFechaActualizacion(LocalDateTime.now());

        Company updatedCompany = companyRepository.save(company);
        return new CompanyDto(updatedCompany);
    }

    /**
     * Actualizar logo de la empresa
     */
    @Transactional
    public void updateCompanyLogo(Long id, MultipartFile file) throws IOException {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada con ID: " + id));

        if (!file.isEmpty()) {
            company.setLogo(file.getBytes());
            company.setFechaActualizacion(LocalDateTime.now());
            companyRepository.save(company);
        }
    }

    /**
     * Actualizar segundo logo de la empresa
     */
    @Transactional
    public void updateCompanyLogo2(Long id, MultipartFile file) throws IOException {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada con ID: " + id));

        if (!file.isEmpty()) {
            company.setLogo2(file.getBytes());
            company.setFechaActualizacion(LocalDateTime.now());
            companyRepository.save(company);
        }
    }

    /**
     * Obtener logo de la empresa
     */
    @Transactional(readOnly = true)
    public Optional<byte[]> getCompanyLogo(Long id) {
        return companyRepository.findById(id)
                .map(Company::getLogo);
    }

    /**
     * Obtener segundo logo de la empresa
     */
    @Transactional(readOnly = true)
    public Optional<byte[]> getCompanyLogo2(Long id) {
        return companyRepository.findById(id)
                .map(Company::getLogo2);
    }

    /**
     * Eliminar empresa
     */
    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("Empresa no encontrada con ID: " + id);
        }
        companyRepository.deleteById(id);
    }

    /**
     * Obtener todas las empresas
     */
    @Transactional(readOnly = true)
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(CompanyDto::new)
                .toList();
    }
}
