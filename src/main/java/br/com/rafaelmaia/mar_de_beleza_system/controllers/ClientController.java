package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.ClientService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/client")
public class ClientController {

    public static final String ID = "/{id}";
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ClientService service;

    @GetMapping(value = ID)
    public ResponseEntity<ClientDTO> findClientById(@PathVariable Long id) {

        return ResponseEntity.ok().body(mapper.map(service.findClientById(id), ClientDTO.class));
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> findAllClients() {

        return ResponseEntity.ok().body(
                service.findAllClients().stream().map(client -> mapper.map(client, ClientDTO.class)).toList()
        );
    }

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO obj) {
        URI uri = ServletUriComponentsBuilder.
                fromCurrentRequest().path(ID).buildAndExpand(service.createClient(obj).getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = ID)
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @RequestBody ClientDTO obj) {
        obj.setId(id);
        return ResponseEntity.ok().body(mapper.map(service.updateClient(obj), ClientDTO.class));
    }
}
