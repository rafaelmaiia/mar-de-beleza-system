package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDTO {

    private Long id;
    private ClientDTO client;
    private ServiceType serviceName;
    private ProfessionalDTO professional;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private String observations;
}
