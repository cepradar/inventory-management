package com.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.model.Cliente;
import com.inventory.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    public Cliente crearCliente(Cliente cliente){
        return repository.save(cliente);
    }    

    public Cliente buscarCliente(Long nitBuscado){
        return repository.findByNit(nitBuscado);
    } 
}
