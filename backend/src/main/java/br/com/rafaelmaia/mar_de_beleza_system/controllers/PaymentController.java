package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.controllers.docs.PaymentControllerDocs;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.dto.PaymentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.PaymentResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.PaymentService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Payment", description = "Endpoints for Managing Payments")
public class PaymentController implements PaymentControllerDocs {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Override
    public ResponseEntity<PaymentResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findPaymentById(id));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Override
    public ResponseEntity<Page<PaymentResponseDTO>> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long professionalId,
            @RequestParam(required = false) PaymentStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(paymentService.findAllPayments(startDate, endDate, professionalId,status, pageable));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Override
    public ResponseEntity<PaymentResponseDTO> create(@RequestBody @Valid PaymentRequestDTO request){
        PaymentResponseDTO newPaymentDTO = paymentService.create(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newPaymentDTO.id())
                .toUri();

        return ResponseEntity.created(uri).body(newPaymentDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Override
    public ResponseEntity<PaymentResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid PaymentRequestDTO request) {

        return ResponseEntity.ok(paymentService.update(id, request));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Apenas admins podem cancelar um pagamento
    @Override
    public ResponseEntity<Void> cancelPayment(@PathVariable Long id) {
        paymentService.cancelPayment(id);

        return ResponseEntity.noContent().build();
    }
}
