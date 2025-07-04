package br.com.rafaelmaia.mar_de_beleza_system.controllers.docs;

import br.com.rafaelmaia.mar_de_beleza_system.dto.SalonServiceRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.SalonServiceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface SalonServiceControllerDocs {

    @Operation(summary = "Find a Salon Service by ID", tags = {"Salon Service"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = SalonServiceResponseDTO.class))),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
    })
    ResponseEntity<SalonServiceResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Find all Salon Services", tags = {"Salon Service"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SalonServiceResponseDTO.class)))),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
    })
    ResponseEntity<List<SalonServiceResponseDTO>> findAll();

    @Operation(summary = "Adds a new Salon Service", description = "Only accessible by ADMIN users.", tags = {"Salon Service"}, responses = {
            @ApiResponse(description = "Created", responseCode = "201", content = @Content(schema = @Schema(implementation = SalonServiceResponseDTO.class))),
            @ApiResponse(description = "Bad Request (Validation Error)", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content)
    })
    ResponseEntity<SalonServiceResponseDTO> create(@RequestBody @Valid SalonServiceRequestDTO requestDTO);

    @Operation(summary = "Updates a Salon Service", description = "Only accessible by ADMIN users.", tags = {"Salon Service"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = SalonServiceResponseDTO.class))),
            @ApiResponse(description = "Bad Request (Validation Error)", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content)
    })
    ResponseEntity<SalonServiceResponseDTO> update(@PathVariable Long id, @RequestBody @Valid SalonServiceRequestDTO requestDTO);

    @Operation(summary = "Deletes a Salon Service", description = "Only accessible by ADMIN users.", tags = {"Salon Service"}, responses = {
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content)
    })
    ResponseEntity<Void> delete(@PathVariable Long id);
}
