package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.ProfessionalService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/professionals")
public class ProfessionalController {

    public static final String ID = "/{id}";
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ProfessionalService service;

    @GetMapping(value = ID)
    public ResponseEntity<ProfessionalDTO> findProfessionalById(@PathVariable Long id) {

        return ResponseEntity.ok().body(mapper.map(service.findProfessionalById(id), ProfessionalDTO.class));
    }

    @GetMapping
    public ResponseEntity<List<ProfessionalDTO>> findAllProfessionals() {

        return ResponseEntity.ok().body(
                service.findAllProfessionals().stream().map(client -> mapper.map(client, ProfessionalDTO.class)).toList()
        );
    }

    @PostMapping
    public ResponseEntity<ProfessionalDTO> createProfessional(@RequestBody ProfessionalDTO obj) {
        URI uri = ServletUriComponentsBuilder.
                fromCurrentRequest().path(ID).buildAndExpand(service.createProfessional(obj).getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = ID)
    public ResponseEntity<ProfessionalDTO> updateProfessional(@PathVariable Long id, @RequestBody ProfessionalDTO obj) {
        obj.setId(id);
        return ResponseEntity.ok().body(mapper.map(service.updateProfessional(obj), ProfessionalDTO.class));
    }

    @DeleteMapping(value = ID)
    public ResponseEntity<ProfessionalDTO> deleteProfessional(@PathVariable Long id) {
        service.deleteProfessional(id);
        return ResponseEntity.noContent().build();
    }
}
