package com.inventory.service;

import com.inventory.dto.ServicioDto;
import com.inventory.model.CategoriaElectrodomestico;
import com.inventory.model.Servicio;
import com.inventory.repository.CategoriaElectrodomesticoRepository;
import com.inventory.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private CategoriaElectrodomesticoRepository categoriaElectrodomesticoRepository;

    /** Lista todos los servicios (activos e inactivos). */
    public List<ServicioDto> listar() {
        return servicioRepository.findAll().stream()
                .map(ServicioDto::new)
                .collect(Collectors.toList());
    }

    /** Lista solo los servicios activos, ordenados por nombre. */
    public List<ServicioDto> listarActivos() {
        return servicioRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(ServicioDto::new)
                .collect(Collectors.toList());
    }

    /** Obtiene un servicio por ID. */
    public ServicioDto obtenerPorId(Long id) {
        return servicioRepository.findById(Objects.requireNonNull(id, "id"))
                .map(ServicioDto::new)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado: " + id));
    }

    /** Crea un nuevo servicio. */
    public ServicioDto crear(ServicioDto dto) {
        String codigo = Objects.requireNonNull(dto.getCodigo(), "codigo").trim().toUpperCase();
        if (codigo.isEmpty()) {
            throw new RuntimeException("El código del servicio es obligatorio");
        }
        if (servicioRepository.existsByCodigo(codigo)) {
            throw new RuntimeException("Ya existe un servicio con el código: " + codigo);
        }
        Objects.requireNonNull(dto.getNombre(), "nombre");

        dto.setCodigo(codigo);
        Servicio servicio = ServicioDto.toServicio(dto);
        resolverCategoriaElectrodomestico(servicio, dto.getCategoriaElectrodomesticoId());
        return new ServicioDto(servicioRepository.save(servicio));
    }

    /** Actualiza un servicio existente. */
    public ServicioDto actualizar(Long id, ServicioDto dto) {
        Servicio servicio = servicioRepository.findById(Objects.requireNonNull(id, "id"))
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado: " + id));

        // Si el código cambia, verificar que no colisione
        String nuevoCodigo = Objects.requireNonNull(dto.getCodigo(), "codigo").trim().toUpperCase();
        if (!nuevoCodigo.equals(servicio.getCodigo()) && servicioRepository.existsByCodigo(nuevoCodigo)) {
            throw new RuntimeException("Ya existe un servicio con el código: " + nuevoCodigo);
        }

        servicio.setCodigo(nuevoCodigo);
        servicio.setNombre(Objects.requireNonNull(dto.getNombre(), "nombre"));
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setPrecioBase(dto.getPrecioBase() != null ? dto.getPrecioBase() : servicio.getPrecioBase());
        servicio.setDuracionEstimadaMinutos(dto.getDuracionEstimadaMinutos());
        servicio.setGarantiaDias(dto.getGarantiaDias() != null ? dto.getGarantiaDias() : servicio.getGarantiaDias());
        servicio.setCategoriaServicio(dto.getCategoriaServicio());
        resolverCategoriaElectrodomestico(servicio, dto.getCategoriaElectrodomesticoId());
        servicio.setActivo(dto.isActivo());

        return new ServicioDto(servicioRepository.save(servicio));
    }

    /** Resuelve y asigna la FK de CategoriaElectrodomestico; si id es null la borra. */
    private void resolverCategoriaElectrodomestico(Servicio servicio, Long catId) {
        if (catId == null) {
            servicio.setCategoriaElectrodomestico(null);
        } else {
            CategoriaElectrodomestico cat = categoriaElectrodomesticoRepository.findById(catId)
                    .orElseThrow(() -> new RuntimeException("Categoría de electrodoméstico no encontrada: " + catId));
            servicio.setCategoriaElectrodomestico(cat);
        }
    }

    /** Elimina (desactiva) un servicio. Solo desactiva para conservar historial. */
    public void desactivar(Long id) {
        Servicio servicio = servicioRepository.findById(Objects.requireNonNull(id, "id"))
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado: " + id));
        servicio.setActivo(false);
        servicioRepository.save(servicio);
    }

    /** Elimina definitivamente un servicio (solo si no hay referencias). */
    public void eliminar(Long id) {
        if (!servicioRepository.existsById(Objects.requireNonNull(id, "id"))) {
            throw new RuntimeException("Servicio no encontrado: " + id);
        }
        try {
            servicioRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(
                "No se puede eliminar el servicio porque tiene ventas u órdenes asociadas. " +
                "Use desactivar en su lugar.");
        }
    }
}
