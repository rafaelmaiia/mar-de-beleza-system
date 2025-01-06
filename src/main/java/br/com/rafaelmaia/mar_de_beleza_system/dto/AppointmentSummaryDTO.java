package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;

import java.time.LocalDateTime;

public class AppointmentSummaryDTO {

    private Long id;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private ProfessionalDTO professionalName; // Nome do profissional associado
    private ServiceType serviceName; // Nome do serviço associado
}
