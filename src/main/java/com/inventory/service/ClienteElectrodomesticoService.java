package com.inventory.service;

import com.inventory.dto.ClienteElectrodomesticoDto;
import com.inventory.model.Cliente;
import com.inventory.model.ClienteElectrodomestico;
import com.inventory.model.MarcaElectrodomestico;
import com.inventory.model.User;
import com.inventory.repository.ClienteElectrodomesticoRepository;
import com.inventory.repository.ClienteRepository;
import com.inventory.repository.MarcaElectrodomesticoRepository;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteElectrodomesticoService {

    @Autowired
    private ClienteElectrodomesticoRepository repo;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarcaElectrodomesticoRepository marcaRepository;

    public ClienteElectrodomesticoDto registrar(ClienteElectrodomesticoDto dto, String username) {
        User usuario = userRepository.findById(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        Cliente cliente = clienteRepository.findById(dto.getClienteId()).orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + dto.getClienteId()));

        // Validar número de serie obligatorio
        if (dto.getNumeroSerie() == null || dto.getNumeroSerie().isBlank()) {
            throw new RuntimeException("Debe ingresar el número de serie del electrodoméstico");
        }

        // Validar que la combinación serial+marca+cliente sea única
        if (dto.getMarcaElectrodomesticoId() != null && dto.getMarcaElectrodomesticoId() > 0) {
            boolean existe = repo.existsByNumeroSerieAndMarcaElectrodomesticoIdAndClienteId(
                dto.getNumeroSerie(), 
                dto.getMarcaElectrodomesticoId(), 
                dto.getClienteId()
            );
            if (existe) {
                throw new RuntimeException("Ya existe un electrodoméstico con este número de serie y marca para este cliente");
            }
        }

        ClienteElectrodomestico ce = new ClienteElectrodomestico();
        ce.setCliente(cliente);
        ce.setElectrodomesticoTipo(dto.getElectrodomesticoTipo());
        
        // Manejar marca desde tabla independiente
        if (dto.getMarcaElectrodomesticoId() != null && dto.getMarcaElectrodomesticoId() > 0) {
            MarcaElectrodomestico marca = marcaRepository.findById(dto.getMarcaElectrodomesticoId())
                .orElseThrow(() -> new RuntimeException("Marca no encontrada: " + dto.getMarcaElectrodomesticoId()));
            ce.setMarcaElectrodomestico(marca);
        }
        
        ce.setElectrodomesticoModelo(dto.getElectrodomesticoModelo());
        ce.setNumeroSerie(dto.getNumeroSerie());
        ce.setUsuario(usuario);

        ClienteElectrodomestico saved = repo.save(ce);
        return convertirADto(saved);
    }

    public ClienteElectrodomesticoDto actualizar(Long id, ClienteElectrodomesticoDto dto) {
        ClienteElectrodomestico ce = repo.findById(id).orElseThrow(() -> new RuntimeException("No encontrado: " + id));
        ce.setElectrodomesticoTipo(dto.getElectrodomesticoTipo());
        
        // Manejar marca desde tabla independiente
        if (dto.getMarcaElectrodomesticoId() != null) {
            MarcaElectrodomestico marca = marcaRepository.findById(dto.getMarcaElectrodomesticoId())
                .orElseThrow(() -> new RuntimeException("Marca no encontrada: " + dto.getMarcaElectrodomesticoId()));
            ce.setMarcaElectrodomestico(marca);
        }
        
        ce.setElectrodomesticoModelo(dto.getElectrodomesticoModelo());
        ce.setColorOFinish(dto.getColorOFinish());
        ce.setEstado(dto.getEstado());
        ce.setNotas(dto.getNotas());
        ce.setGarantiaVigente(dto.getGarantiaVigente());
        ce.setFechaVencimientoGarantia(dto.getFechaVencimientoGarantia());
        ClienteElectrodomestico updated = repo.save(ce);
        return convertirADto(updated);
    }

    public ClienteElectrodomesticoDto obtenerPorId(Long id) {
        ClienteElectrodomestico ce = repo.findById(id).orElseThrow(() -> new RuntimeException("No encontrado: " + id));
        return convertirADto(ce);
    }

    public List<ClienteElectrodomesticoDto> listarPorCliente(String clienteId) {
        return repo.findByClienteId(clienteId).stream().map(this::convertirADto).collect(Collectors.toList());
    }

    public List<ClienteElectrodomesticoDto> listarTodos() {
        return repo.findAll().stream().map(this::convertirADto).collect(Collectors.toList());
    }

    public void eliminar(Long id) {
        ClienteElectrodomestico ce = repo.findById(id).orElseThrow(() -> new RuntimeException("No encontrado: " + id));
        repo.delete(ce);
    }

    private ClienteElectrodomesticoDto convertirADto(ClienteElectrodomestico ce) {
        ClienteElectrodomesticoDto dto = new ClienteElectrodomesticoDto();
        dto.setId(ce.getId());
        dto.setClienteId(ce.getCliente().getId());
        dto.setClienteNombre(ce.getCliente().getNombre());
        dto.setClienteTelefono(ce.getCliente().getTelefono());
        dto.setElectrodomesticoTipo(ce.getElectrodomesticoTipo());
        
        // Manejar marca desde tabla independiente
        if (ce.getMarcaElectrodomestico() != null) {
            dto.setMarcaElectrodomesticoId(ce.getMarcaElectrodomestico().getId());
            dto.setMarcaElectrodomesticoNombre(ce.getMarcaElectrodomestico().getNombre());
        }

        dto.setElectrodomesticoModelo(ce.getElectrodomesticoModelo());
        dto.setNumeroSerie(ce.getNumeroSerie());
        dto.setColorOFinish(ce.getColorOFinish());
        dto.setEstado(ce.getEstado());
        dto.setFechaAdquisicion(ce.getFechaAdquisicion());
        dto.setFechaRegistro(ce.getFechaRegistro());
        dto.setGarantiaVigente(ce.getGarantiaVigente());
        dto.setFechaVencimientoGarantia(ce.getFechaVencimientoGarantia());
        dto.setNotas(ce.getNotas());
        dto.setUsuarioUsername(ce.getUsuario() != null ? ce.getUsuario().getUsername() : null);
        dto.setUsuarioNombre(ce.getUsuario() != null ? ce.getUsuario().getFirstName() + " " + ce.getUsuario().getLastName() : null);
        return dto;
    }
}
