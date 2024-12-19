package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.ClientService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/client")
public class ClientController {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ClientService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<ClientDTO> findClientById(@PathVariable Long id) {

        return ResponseEntity.ok().body(mapper.map(service.findClientById(id), ClientDTO.class));
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> findAllClients() {

        return ResponseEntity.ok().body(
                service.findAllClients().stream().map(client -> mapper.map(client, ClientDTO.class)).toList()
        );
    }
}
