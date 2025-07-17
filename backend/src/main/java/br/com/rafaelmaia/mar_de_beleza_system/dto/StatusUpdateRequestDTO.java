package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequestDTO(
        @NotNull(message = "O status não pode ser nulo")
        AppointmentStatus status
) {}
