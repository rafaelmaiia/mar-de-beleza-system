package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.controllers.docs.ClientControllerDocs;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.ClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/clients")
@Tag(name = "Client", description = "Endpoints for Managing Clients")
public class ClientController implements ClientControllerDocs {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ClientService service;

    @GetMapping(value = ID)
    @Override
    public ResponseEntity<ClientDTO> findClientById(@PathVariable Long id) {

        return ResponseEntity.ok().body(mapper.map(service.findClientById(id), ClientDTO.class));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ClientDTO>> findAllClients() {

        return ResponseEntity.ok().body(
                service.findAllClients().stream().map(client -> mapper.map(client, ClientDTO.class)).toList()
        );
    }

    @PostMapping
    @Override
    public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO obj) {
        URI uri = ServletUriComponentsBuilder.
                fromCurrentRequest().path(ID).buildAndExpand(service.createClient(obj).getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = ID)
    @Override
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @RequestBody ClientDTO obj) {
        obj.setId(id);
        return ResponseEntity.ok().body(mapper.map(service.updateClient(obj), ClientDTO.class));
    }

    @DeleteMapping(value = ID)
    @Override
    public ResponseEntity<?> deleteClient(@PathVariable("id") Long id) {
        service.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
