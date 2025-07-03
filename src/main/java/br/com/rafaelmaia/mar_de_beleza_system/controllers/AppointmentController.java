package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.controllers.docs.AppointmentControllerDocs;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.AppointmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Appointment", description = "Endpoints for Managing Appointment")
public class AppointmentController implements AppointmentControllerDocs {

    private final AppointmentService appointmentService;

    @PostMapping
    @Override
    public ResponseEntity<AppointmentDTO> create(@RequestBody @Valid AppointmentRequestDTO request) {
        AppointmentDTO created = appointmentService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<AppointmentDTO>> findAll(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,
            @RequestParam(value = "professionalId", required = false) Long professionalId,
            @RequestParam(value = "clientId", required = false) Long clientId) {
        return ResponseEntity.ok(appointmentService.findAllAppointments(date, professionalId, clientId));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<AppointmentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findAppointmentById(id));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<AppointmentDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid AppointmentRequestDTO request) {
        return ResponseEntity.ok(appointmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

