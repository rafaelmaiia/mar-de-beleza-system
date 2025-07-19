package br.com.rafaelmaia.mar_de_beleza_system.controllers.docs;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ProfessionalControllerDocs {

    @Operation(summary = "Find a Professional by ID",
            description = "Find a specific Professional by their ID",
            tags = {"Professional"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = ProfessionalResponseDTO.class))),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
            }
    )
    ResponseEntity<ProfessionalResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Find All Professionals",
            description = "Find All Professionals",
            tags = {"Professional"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProfessionalResponseDTO.class)))),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
            }
    )
    ResponseEntity<List<ProfessionalResponseDTO>> findAll(@RequestParam(value = "specialty", required = false) ServiceType specialty);

    @Operation(summary = "Adds a new Professional",
            description = "Adds a new Professional. Only accessible by ADMIN users.",
            tags = {"Professional"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201", content = @Content(schema = @Schema(implementation = ProfessionalResponseDTO.class))),
                    @ApiResponse(description = "Bad Request (Validation Error)", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content)
            }
    )
    ResponseEntity<ProfessionalResponseDTO> create(@RequestBody @Valid ProfessionalRequestDTO requestDTO);

    @Operation(summary = "Updates a Professional's information",
            description = "Updates a Professional's information. Only accessible by ADMIN users.",
            tags = {"Professional"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = ProfessionalResponseDTO.class))),
                    @ApiResponse(description = "Bad Request (Validation Error)", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content)
            }
    )
    ResponseEntity<ProfessionalResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ProfessionalRequestDTO requestDTO);

    @Operation(summary = "Deletes a Professional",
            description = "Deletes a specific Professional by their ID. Only accessible by ADMIN users.",
            tags = {"Professional"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content)
            }
    )
    ResponseEntity<Void> delete(@PathVariable Long id);
}