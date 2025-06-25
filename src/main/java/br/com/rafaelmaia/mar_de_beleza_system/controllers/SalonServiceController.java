package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.controllers.docs.SalonServiceControllerDocs;
import br.com.rafaelmaia.mar_de_beleza_system.dto.SalonServiceDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.SalonServiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/SalonServices")
@Tag(name = "SalonService", description = "Endpoints for Managing Salon Services")
public class SalonServiceController implements SalonServiceControllerDocs {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private SalonServiceService service;

    @GetMapping(value = ID)
    @Override
    public ResponseEntity<SalonServiceDTO> findSalonServiceById(@PathVariable Long id) {

        return ResponseEntity.ok().body(mapper.map(service.findSalonServiceById(id), SalonServiceDTO.class));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<SalonServiceDTO>> findAllSalonServices() {

        return ResponseEntity.ok().body(
                service.findAllSalonServices().stream().map(SalonService -> mapper.map(SalonService, SalonServiceDTO.class)).toList()
        );
    }

    @PostMapping
    @Override
    public ResponseEntity<SalonServiceDTO> createSalonService(@RequestBody SalonServiceDTO obj) {
        URI uri = ServletUriComponentsBuilder.
                fromCurrentRequest().path(ID).buildAndExpand(service.createSalonService(obj).getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = ID)
    @Override
    public ResponseEntity<SalonServiceDTO> updateSalonService(@PathVariable Long id, @RequestBody SalonServiceDTO obj) {
        obj.setId(id);
        return ResponseEntity.ok().body(mapper.map(service.updateSalonService(obj), SalonServiceDTO.class));
    }

    @DeleteMapping(value = ID)
    @Override
    public ResponseEntity<?> deleteSalonService(@PathVariable("id") Long id) {
        service.deleteSalonService(id);
        return ResponseEntity.noContent().build();
    }
}
