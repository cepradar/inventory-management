package com.inventory.service;

import com.inventory.dto.MarcaElectrodomesticoDto;
import com.inventory.model.MarcaElectrodomestico;
import com.inventory.repository.MarcaElectrodomesticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarcaElectrodomesticoService {

    @Autowired
    private MarcaElectrodomesticoRepository repository;

    public List<MarcaElectrodomesticoDto> listarTodas() {
        return repository.findAll().stream()
                .map(MarcaElectrodomesticoDto::new)
                .collect(Collectors.toList());
    }

    public MarcaElectrodomesticoDto crear(MarcaElectrodomesticoDto dto) {
        MarcaElectrodomestico marca = new MarcaElectrodomestico();
        marca.setNombre(dto.getNombre());
        marca.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        MarcaElectrodomestico saved = repository.save(marca);
        return new MarcaElectrodomesticoDto(saved);
    }

    public MarcaElectrodomesticoDto actualizar(Long id, MarcaElectrodomesticoDto dto) {
        MarcaElectrodomestico marca = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marca no encontrada: " + id));
        marca.setNombre(dto.getNombre());
        marca.setActivo(dto.getActivo());
        MarcaElectrodomestico updated = repository.save(marca);
        return new MarcaElectrodomesticoDto(updated);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
