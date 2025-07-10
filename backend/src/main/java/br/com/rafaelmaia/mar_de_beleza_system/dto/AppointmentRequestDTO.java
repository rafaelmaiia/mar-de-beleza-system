package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record AppointmentRequestDTO(
        @NotNull(message = "O ID do cliente não pode ser nulo")
        Long clientId,

        @NotNull(message = "A data do agendamento não pode ser nula")
        @FutureOrPresent(message = "A data do agendamento não pode ser no passado")
        LocalDateTime appointmentDate,

        String observations,

        AppointmentStatus status,

        @NotEmpty(message = "A lista de serviços não pode ser vazia")
        @Valid
        List<AppointmentItemRequestDTO> items
) {}
