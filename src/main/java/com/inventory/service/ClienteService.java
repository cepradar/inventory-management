package com.inventory.service;

import java.util.List;
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
        cliente.setId(clienteDto.getDocumento()); // El documento es el ID
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return new ClienteDto(clienteGuardado);
    }

    public List<ClienteDto> listarClientes(){
        return clienteRepository.findAll().stream()
                .map(ClienteDto::new)
                .collect(Collectors.toList());
    }

    public Optional<ClienteDto> buscarCliente(String id){
        Optional<Cliente> cli = clienteRepository.findById(id);
        return cli.map(ClienteDto::new);
    }

    public ClienteDto actualizarCliente(String id, ClienteDto clienteDto){
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        // Actualizar campos
        clienteExistente.setNombre(clienteDto.getNombre());
        clienteExistente.setTelefono(clienteDto.getTelefono());
        clienteExistente.setDireccion(clienteDto.getDireccion());
        clienteExistente.setActivo(clienteDto.getActivo());
        
        // Actualizar categoría si cambió
        if (clienteDto.getCategoryId() != null) {
            CategoryClient category = categoryClientRepository.findById(clienteDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            clienteExistente.setCategory(category);
        }

        // Actualizar tipo documento si cambió
        if (clienteDto.getTipoDocumentoId() != null) {
            DocumentoTipo tipoDoc = documentoTipoRepository.findById(clienteDto.getTipoDocumentoId())
                    .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));
            clienteExistente.setTipoDocumento(tipoDoc);
        }
        
        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        return new ClienteDto(clienteActualizado);
    }

    public void eliminarCliente(String id){
        clienteRepository.deleteById(id);
    }

    private Cliente convertirDtoAEntidad(ClienteDto clienteDto){
        Cliente cliente = new Cliente();
        cliente.setId(clienteDto.getDocumento());
        cliente.setNit(clienteDto.getDocumento()); // Usar el documento como NIT también
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
        DocumentoTipo tipoDoc = documentoTipoRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay tipos de documento disponibles"));
        cliente.setTipoDocumento(tipoDoc);
        
        return cliente;
    }
}
