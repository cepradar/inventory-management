package com.inventory.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.dto.ClienteDto;
import com.inventory.model.Cliente;
import com.inventory.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    public Cliente crearCliente(Cliente cliente){
        return repository.save(cliente);
    }    

    public Optional<ClienteDto> buscarCliente(String id){
        Optional<Cliente> cli = repository.findById(id);

        return cli.map(ClienteDto::new);
    } 
}
