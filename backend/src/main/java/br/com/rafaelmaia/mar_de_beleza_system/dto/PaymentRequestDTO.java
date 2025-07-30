package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        @NotNull(message = "O ID do agendamento não pode ser nulo")
        Long appointmentId,

        @NotNull(message = "O valor do pagamento é obrigatório")
        @Positive(message = "O valor deve ser um valor positivo")
        BigDecimal totalAmount,

        @NotNull(message = "A forma de pagamento é obrigatória")
        PaymentMethod paymentMethod,

        String observations
) {}
