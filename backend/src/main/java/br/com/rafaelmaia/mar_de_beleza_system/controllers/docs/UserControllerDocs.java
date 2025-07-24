package br.com.rafaelmaia.mar_de_beleza_system.controllers.docs;

import br.com.rafaelmaia.mar_de_beleza_system.dto.UserRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UserControllerDocs {

    @Operation(summary = "Find all system users", tags = {"User Management"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forbidden", responseCode = "403")
    })
    ResponseEntity<List<UserResponseDTO>> findAll();

    @Operation(summary = "Find a user by ID", tags = {"User Management"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forbidden", responseCode = "403")
    })
    ResponseEntity<UserResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Create a new system user", description = "Only accessible by ADMIN users.", tags = {"User Management"}, responses = {
            @ApiResponse(description = "Created", responseCode = "201"),
            @ApiResponse(description = "Bad Request (Validation Error)", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forbidden", responseCode = "403")
    })
    ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO requestDTO);

    @Operation(summary = "Update an existing user", description = "Admins can update any user. Staff can only update their own profile.", tags = {"User Management"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
            @ApiResponse(description = "Bad Request (Validation Error)", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forbidden", responseCode = "403")
    })
    ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequestDTO requestDTO);

    @Operation(summary = "Delete a user by ID", description = "Only accessible by ADMIN users.", tags = {"User Management"}, responses = {
            @ApiResponse(description = "No Content", responseCode = "204"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forbidden", responseCode = "403")
    })
    ResponseEntity<Void> deleteUser(@PathVariable Long id);
}