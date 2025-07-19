package br.com.rafaelmaia.mar_de_beleza_system.controllers.docs;

import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.StatusUpdateRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentControllerDocs {

    @Operation(summary = "Adds a new Appointment",
            description = "Adds a new Appointment by passing in a JSON representation of the Appointment.",
            tags = {"Appointment"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<AppointmentResponseDTO> create(@RequestBody @Valid AppointmentRequestDTO request);

    @Operation(summary = "Buscar agendamentos (com ou sem filtros)",
            description = "Retorna uma lista de agendamentos.\n" +
                    "Caso nenhum filtro seja informado, todos os agendamentos cadastrados serão retornados.\n" +
                    "É possível aplicar um ou mais dos seguintes filtros:\n" +
                    "\n" +
                    "- date: data específica do agendamento (yyyy-MM-dd)\n" +
                    "- professionalId: identificador do profissional\n" +
                    "- clientId: identificador do cliente\n" +
                    "\n" +
                    "Os filtros são opcionais e podem ser combinados entre si.",
            tags = {"Appointment"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = AppointmentResponseDTO.class))
                                    )
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Page<AppointmentResponseDTO>> findAll(LocalDate date, Long professionalId, Long clientId, Pageable pageable);

    @Operation(summary = "Find a Appointment",
            description = "Find a specific Appointment by their ID",
            tags = {"Appointment"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<AppointmentResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Updates a Appointment's information",
            description = "Updates a Appointment's information by passing in a JSON representation of the updated Appointment.",
            tags = {"Appointment"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<AppointmentResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid AppointmentRequestDTO request);

    @Operation(summary = "Deletes a Appointment",
            description = "Deletes a specific Appointment by their ID",
            tags = {"Appointment"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> delete(@PathVariable Long id);

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualiza apenas o status de um agendamento",
            description = "Endpoint rápido para modificar apenas o status de um agendamento existente (ex: para Confirmado, Concluído, etc.). Acessível por qualquer usuário autenticado.",
            tags = {"Appointment"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
                    @ApiResponse(description = "Bad Request (Validation Error)", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content)
            }
    )
    ResponseEntity<AppointmentResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid StatusUpdateRequestDTO statusUpdateDTO);

}