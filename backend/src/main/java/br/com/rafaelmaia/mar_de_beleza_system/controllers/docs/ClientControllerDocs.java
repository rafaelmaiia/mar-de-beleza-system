package br.com.rafaelmaia.mar_de_beleza_system.controllers.docs;

import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ClientControllerDocs {

    @Operation(summary = "Find a Client by ID", tags = {"Client"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
    })
    ResponseEntity<ClientResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Find all Clients", tags = {"Client"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClientResponseDTO.class)))),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
    })
    ResponseEntity<Page<ClientResponseDTO>> findAll(Pageable pageable);

    @Operation(summary = "Adds a new Client", tags = {"Client"}, responses = {
            @ApiResponse(description = "Created", responseCode = "201", content = @Content(schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(description = "Bad Request (Validation Error)", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
    })
    ResponseEntity<ClientResponseDTO> create(@RequestBody @Valid ClientRequestDTO requestDTO);

    @Operation(summary = "Updates a Client's information", tags = {"Client"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(description = "Bad Request (Validation Error)", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content)
    })
    ResponseEntity<ClientResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ClientRequestDTO requestDTO);

    @Operation(summary = "Deletes a Client", description = "Deletes a specific Client by their ID. May be restricted to ADMIN users.", tags = {"Client"}, responses = {
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content)
    })
    ResponseEntity<Void> delete(@PathVariable Long id);
}
