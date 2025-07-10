package br.com.rafaelmaia.mar_de_beleza_system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AppointmentItemRequestDTO(
        @NotNull(message = "O ID do serviço não pode ser nulo")
        Long salonServiceId,

        @NotNull(message = "O ID do profissional não pode ser nulo")
        Long professionalId,

        @Positive(message = "O preço deve ser um valor positivo")
        BigDecimal price
) {}
