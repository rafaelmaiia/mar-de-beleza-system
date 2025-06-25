package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Appointment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDTO {

    private Long id;
    private ClientDTO client;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private String observations;
    private LocalDateTime createdAt;
    private List<AppointmentItemDTO> items;

    public static AppointmentDTO fromEntity(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .client(ClientDTO.fromEntity(appointment.getClient())) // Assumindo que ClientDTO.fromEntity existe
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .observations(appointment.getObservations())
                .createdAt(appointment.getCreatedAt())
                .items(appointment.getServices().stream()
                        .map(AppointmentItemDTO::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
