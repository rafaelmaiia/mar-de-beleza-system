package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AppointmentRequestDTO(
        @NotNull(message = "O ID do cliente não pode ser nulo")
        Long clientId,

        @NotNull(message = "A data do agendamento não pode ser nula")
        @FutureOrPresent(message = "A data do agendamento não pode ser no passado")
        LocalDateTime appointmentDate,

        @NotNull(message = "O ID do serviço não pode ser nulo")
        Long salonServiceId,

        @NotNull(message = "O ID do profissional não pode ser nulo")
        Long professionalId,

        @Positive(message = "O preço deve ser um valor positivo")
        BigDecimal price,

        String observations,

        AppointmentStatus status
) {}
