package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.controllers.docs.ProfessionalControllerDocs;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.ProfessionalService;
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
@RequestMapping(value = "/api/v1/professionals")
@RequiredArgsConstructor
@Tag(name = "Professional", description = "Endpoints for Managing Professionals")
public class ProfessionalController implements ProfessionalControllerDocs {

    private final ProfessionalService service;

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ProfessionalResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findProfessionalById(id));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ProfessionalResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAllProfessionals());
    }

    @PostMapping
    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProfessionalResponseDTO> create(@RequestBody @Valid ProfessionalRequestDTO requestDTO) {
        ProfessionalResponseDTO newDto = service.createProfessional(requestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newDto.id()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PutMapping("/{id}")
    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProfessionalResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ProfessionalRequestDTO requestDTO) {
        return ResponseEntity.ok(service.updateProfessional(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProfessional(id);
        return ResponseEntity.noContent().build();
    }
}