package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.dto.PaymentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.PaymentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PaymentService {
    PaymentResponseDTO findPaymentById(Long id);
    Page<PaymentResponseDTO> findAllPayments(LocalDate startDate, LocalDate endDate, Long professionalId, PaymentStatus status, Pageable pageable);
    PaymentResponseDTO create(PaymentRequestDTO obj);
    PaymentResponseDTO update(Long id, PaymentRequestDTO obj);
    void cancelPayment(Long id);
}
