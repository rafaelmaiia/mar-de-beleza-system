package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Appointment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppointmentResponseDTO(
        Long id,
        ClientResponseDTO client,
        SalonServiceResponseDTO service,
        UserResponseDTO professional,
        LocalDateTime appointmentDate,
        AppointmentStatus status,
        String observations,
        BigDecimal price
) {

    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return new AppointmentResponseDTO(
            appointment.getId(),
            ClientResponseDTO.fromEntity(appointment.getClient()),
            SalonServiceResponseDTO.fromEntity(appointment.getService()),
            UserResponseDTO.fromEntity(appointment.getProfessional()),
            appointment.getAppointmentDate(),
            appointment.getStatus(),
            appointment.getObservations(),
            appointment.getPrice()
        );
    }
}
