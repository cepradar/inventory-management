package com.inventory.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.dto.ClienteDto;
import com.inventory.model.CategoryClient;
import com.inventory.model.Cliente;
import com.inventory.model.DocumentoTipo;
import com.inventory.repository.CategoryClientRepository;
import com.inventory.repository.ClienteRepository;
import com.inventory.repository.DocumentoTipoRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CategoryClientRepository categoryClientRepository;

    @Autowired
    private DocumentoTipoRepository documentoTipoRepository;

    public ClienteDto crearCliente(ClienteDto clienteDto){
        Cliente cliente = convertirDtoAEntidad(clienteDto);
        cliente.setId(clienteDto.getDocumento()); // El documento es parte de la PK
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return new ClienteDto(clienteGuardado);
    }

    public List<ClienteDto> listarClientes(){
        return clienteRepository.findAll().stream()
                .map(ClienteDto::new)
                .collect(Collectors.toList());
    }

    public List<ClienteDto> buscarClientesPorDocumento(String documento){
        if (documento == null || documento.trim().isEmpty()) {
            return List.of();
        }
        return clienteRepository.findByDocumento(documento).stream()
                .map(ClienteDto::new)
                .collect(Collectors.toList());
    }

    public Optional<ClienteDto> buscarCliente(String documento, String tipoDocumentoId){
        if (documento == null || documento.trim().isEmpty() || tipoDocumentoId == null || tipoDocumentoId.trim().isEmpty()) {
            return Optional.empty();
        }
        Optional<Cliente> cli = clienteRepository.findByIdAndTipoDocumentoId(documento, tipoDocumentoId);
        return cli.map(ClienteDto::new);
    }

    public ClienteDto actualizarCliente(String documento, String tipoDocumentoId, ClienteDto clienteDto){
        if (documento == null || documento.trim().isEmpty() || tipoDocumentoId == null || tipoDocumentoId.trim().isEmpty()) {
            throw new RuntimeException("Documento y tipo de documento son obligatorios");
        }
        Cliente clienteExistente = clienteRepository.findByIdAndTipoDocumentoId(documento, tipoDocumentoId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        // Actualizar campos
        clienteExistente.setNombre(clienteDto.getNombre());
        clienteExistente.setTelefono(clienteDto.getTelefono());
        clienteExistente.setDireccion(clienteDto.getDireccion());
        clienteExistente.setActivo(clienteDto.getActivo());
        
        // Actualizar categoría si cambió
        if (clienteDto.getCategoryId() != null) {
            String categoryId = Objects.requireNonNull(clienteDto.getCategoryId(), "categoryId");
            CategoryClient category = categoryClientRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            clienteExistente.setCategory(category);
        }

        // Actualizar tipo documento si cambió
        if (clienteDto.getTipoDocumentoId() != null) {
            String tipoDocId = Objects.requireNonNull(clienteDto.getTipoDocumentoId(), "tipoDocumentoId");
            DocumentoTipo tipoDoc = documentoTipoRepository.findById(tipoDocId)
                    .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));
            clienteExistente.setTipoDocumento(tipoDoc);
        }
        
        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        return new ClienteDto(clienteActualizado);
    }

    public void eliminarCliente(String documento, String tipoDocumentoId){
        if (documento == null || documento.trim().isEmpty() || tipoDocumentoId == null || tipoDocumentoId.trim().isEmpty()) {
            throw new RuntimeException("Documento y tipo de documento son obligatorios");
        }
        Cliente clienteExistente = clienteRepository.findByIdAndTipoDocumentoId(documento, tipoDocumentoId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        clienteRepository.delete(clienteExistente);
    }

    private Cliente convertirDtoAEntidad(ClienteDto clienteDto){
        Cliente cliente = new Cliente();
        cliente.setId(clienteDto.getDocumento());
        cliente.setNit(clienteDto.getNit() != null ? clienteDto.getNit() : clienteDto.getDocumento());
        cliente.setNombre(clienteDto.getNombre());
        cliente.setTelefono(clienteDto.getTelefono());
        cliente.setDireccion(clienteDto.getDireccion());
        cliente.setActivo(true); // Por defecto activo al crear
        
        // Buscar y asignar la categoría por defecto "General" o la primera disponible
        CategoryClient category = categoryClientRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay categorías de cliente disponibles"));
        cliente.setCategory(category);
        
        // Buscar y asignar tipo documento por defecto "CC" o el primero disponible
        DocumentoTipo tipoDoc;
        if (clienteDto.getTipoDocumentoId() != null && !clienteDto.getTipoDocumentoId().isEmpty()) {
            String tipoDocId = Objects.requireNonNull(clienteDto.getTipoDocumentoId(), "tipoDocumentoId");
            tipoDoc = documentoTipoRepository.findById(tipoDocId)
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));
        } else {
            tipoDoc = documentoTipoRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay tipos de documento disponibles"));
        }
        cliente.setTipoDocumento(tipoDoc);
        
        return cliente;
    }
}
