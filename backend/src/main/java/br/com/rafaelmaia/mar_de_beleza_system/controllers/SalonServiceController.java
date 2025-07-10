package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.controllers.docs.SalonServiceControllerDocs;
import br.com.rafaelmaia.mar_de_beleza_system.dto.SalonServiceRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.SalonServiceResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.SalonServiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/SalonServices")
@RequiredArgsConstructor
@Tag(name = "SalonService", description = "Endpoints for Managing Salon Services")
public class SalonServiceController implements SalonServiceControllerDocs {

    private final SalonServiceService service;

    @GetMapping("/{id}")
    public ResponseEntity<SalonServiceResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findServiceById(id));
    }

    @GetMapping
    public ResponseEntity<List<SalonServiceResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAllServices());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Apenas Admins podem cadastrar serviços
    public ResponseEntity<SalonServiceResponseDTO> create(@RequestBody @Valid SalonServiceRequestDTO requestDTO) {
        SalonServiceResponseDTO newDto = service.createService(requestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newDto.id()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SalonServiceResponseDTO> update(@PathVariable Long id, @RequestBody @Valid SalonServiceRequestDTO requestDTO) {
        return ResponseEntity.ok(service.updateService(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
