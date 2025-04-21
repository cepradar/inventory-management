package com.inventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.dto.EventoProductoDto;
import com.inventory.model.Cliente;
import com.inventory.model.EventoProducto;
import com.inventory.model.Product;
import com.inventory.model.TipoEvento;
import com.inventory.model.User;
import com.inventory.repository.ClienteRepository;
import com.inventory.repository.EventoProductoRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.TipoEventoRepository;
import com.inventory.repository.UserRepository;

@Service
public class EventoProductoService {

    @Autowired
    private EventoProductoRepository repository;

    public EventoProducto registrarEvento(EventoProducto evento) {
        return repository.save(evento);
    }

    public List<EventoProducto> obtenerEventosPorProducto(Long productoId) {
        return repository.findByProducto_Id(productoId);
    }

    @Autowired
private UserRepository userRepository;

@Autowired
private TipoEventoRepository tipoEventoRepository;

@Autowired
private ProductRepository productRepository;

@Autowired
private ClienteRepository clienteRepository;

@Autowired
private EventoProductoRepository eventoRepository;

public EventoProducto registrarDesdeDTO(EventoProductoDto dto) {
    EventoProducto evento = new EventoProducto();

    User usuario = userRepository.findById(dto.getUsuarioUsername())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    TipoEvento tipo = tipoEventoRepository.findById(dto.getTipoDeEventoId())
        .orElseThrow(() -> new RuntimeException("Tipo de evento no encontrado"));

    Product producto = productRepository.findById(dto.getProductoId())
        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    evento.setUsuario(usuario);
    evento.setTipoDeEvento(tipo);
    evento.setProducto(producto);
    evento.setFechaEvento(dto.getFechaEvento() != null ? dto.getFechaEvento() : LocalDateTime.now());

    if (dto.getClienteNit() != null) {
        Cliente cliente = clienteRepository.findById(dto.getClienteNit())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        evento.setCliente(cliente);
    }

    evento.setCantidad(dto.getCantidad());
    evento.setObservacion(dto.getObservacion());

    return eventoRepository.save(evento);
}

}
