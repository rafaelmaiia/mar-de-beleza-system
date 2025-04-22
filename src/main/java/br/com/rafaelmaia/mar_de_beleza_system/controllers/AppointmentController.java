package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentDTO> create(@RequestBody @Valid AppointmentRequestDTO request) {
        AppointmentDTO created = appointmentService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> findAll() {
        return ResponseEntity.ok(appointmentService.findAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findAppointmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid AppointmentRequestDTO request) {
        return ResponseEntity.ok(appointmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

