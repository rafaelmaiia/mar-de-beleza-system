package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Appointment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record AppointmentResponseDTO(
        Long id,
        ClientResponseDTO client,
        LocalDateTime appointmentDate,
        AppointmentStatus status,
        String observations,
        List<AppointmentItemResponseDTO> items
) {

    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return new AppointmentResponseDTO(
            appointment.getId(),
            ClientResponseDTO.fromEntity(appointment.getClient()),
            appointment.getAppointmentDate(),
            appointment.getStatus(),
            appointment.getObservations(),
            appointment.getServices().stream()
                    .map(AppointmentItemResponseDTO::fromEntity)
                    .collect(Collectors.toList())
        );
    }
}
