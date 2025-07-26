package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.controllers.docs.AppointmentControllerDocs;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.StatusUpdateRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.AppointmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Appointment", description = "Endpoints for Managing Appointment")
public class AppointmentController implements AppointmentControllerDocs {

    private final AppointmentService appointmentService;

    @PostMapping
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AppointmentResponseDTO> create(@RequestBody @Valid AppointmentRequestDTO request) {
        AppointmentResponseDTO newAppointmentDTO = appointmentService.create(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newAppointmentDTO.id())
                .toUri();

        return ResponseEntity.created(uri).body(newAppointmentDTO);
    }

    @GetMapping
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AppointmentResponseDTO>> findAll(
            // Parâmetros para o filtro de intervalo de datas (pag. de Gerenciamento)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            // Parâmetro para o filtro de dia único (Dashboard)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            // Outros filtros
            @RequestParam(required = false) Long professionalId,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String status,

            Pageable pageable) {

        // --- LÓGICA DE DECISÃO ---
        // Se o parâmetro 'date' foi enviado (pelo Dashboard), usamos a busca simples por dia.
        if (date != null) {
            return ResponseEntity.ok(appointmentService.findAppointmentsByDate(date, pageable));
        }

        // Senão, usamos a busca avançada com todos os filtros (pela pág. de Gerenciamento).
        return ResponseEntity.ok(appointmentService.findAllAppointments(startDate, endDate, professionalId, clientId, status, pageable));
    }

    @GetMapping("/{id}")
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AppointmentResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findAppointmentById(id));
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AppointmentResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid AppointmentRequestDTO request) {
        return ResponseEntity.ok(appointmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    @Override
    public ResponseEntity<AppointmentResponseDTO> updateStatus(@PathVariable Long id, @RequestBody @Valid StatusUpdateRequestDTO statusUpdateDTO) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, statusUpdateDTO));
    }
}

