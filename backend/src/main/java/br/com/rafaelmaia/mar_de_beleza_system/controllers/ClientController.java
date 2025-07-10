package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.controllers.docs.ClientControllerDocs;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.ClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Client", description = "Endpoints for Managing Clients")
public class ClientController implements ClientControllerDocs {

    private final ClientService service;

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ClientResponseDTO> findById(@PathVariable Long id) {

        return ResponseEntity.ok().body(service.findClientById(id));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ClientResponseDTO>> findAll() {

        return ResponseEntity.ok().body(service.findAllClients());
    }

    @PostMapping
    @Override
    public ResponseEntity<ClientResponseDTO> create(@RequestBody @Valid ClientRequestDTO requestDTO) {
        ClientResponseDTO newClientDTO = service.createClient(requestDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newClientDTO.id())
                .toUri();

        return ResponseEntity.created(uri).body(newClientDTO);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ClientRequestDTO requestDTO) {
        ClientResponseDTO updatedDto = service.updateClient(id, requestDTO);
        return ResponseEntity.ok().body(updatedDto);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
