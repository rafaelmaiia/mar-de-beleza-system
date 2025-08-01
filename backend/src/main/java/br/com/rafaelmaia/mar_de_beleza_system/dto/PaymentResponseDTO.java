package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Payment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentMethod;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDTO(
        Long id,
        AppointmentResponseDTO appointment,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        LocalDateTime paymentDate,
        PaymentStatus status,
        String observations
) {

    public static PaymentResponseDTO fromEntity(Payment payment) {
        if (payment == null) {
            return null;
        }

        return new PaymentResponseDTO(
                payment.getId(),
                AppointmentResponseDTO.fromEntity(payment.getAppointment()),
                payment.getTotalAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentDate(),
                payment.getStatus(),
                payment.getObservations()
        );
    }
}
