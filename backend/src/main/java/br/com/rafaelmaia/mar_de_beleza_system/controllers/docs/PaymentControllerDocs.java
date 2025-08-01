package br.com.rafaelmaia.mar_de_beleza_system.controllers.docs;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.dto.PaymentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.PaymentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

public interface PaymentControllerDocs {

    @Operation(summary = "Find payment by ID", tags = {"Payment"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
            @ApiResponse(description = "Unauthorized", responseCode = "401")
    })
    ResponseEntity<PaymentResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Find all payments with filters and pagination", tags = {"Payment"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Unauthorized", responseCode = "401")
    })
    ResponseEntity<Page<PaymentResponseDTO>> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long professionalId,
            @RequestParam(required = false) PaymentStatus status,
            Pageable pageable);

    @Operation(summary = "Create a new payment", tags = {"Payment"}, responses = {
            @ApiResponse(description = "Created", responseCode = "201"),
            @ApiResponse(description = "Bad Request", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401")
    })
    ResponseEntity<PaymentResponseDTO> create(@RequestBody @Valid PaymentRequestDTO request);

    @Operation(summary = "Update a payment", tags = {"Payment"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
            @ApiResponse(description = "Bad Request", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401")
    })
    ResponseEntity<PaymentResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid PaymentRequestDTO request);

    @Operation(summary = "Cancel a payment", description = "Sets the payment status to CANCELED. Only accessible by ADMIN users.", tags = {"Payment"}, responses = {
            @ApiResponse(description = "No Content", responseCode = "204"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forbidden", responseCode = "403")
    })
    ResponseEntity<Void> cancelPayment(@PathVariable Long id);
}