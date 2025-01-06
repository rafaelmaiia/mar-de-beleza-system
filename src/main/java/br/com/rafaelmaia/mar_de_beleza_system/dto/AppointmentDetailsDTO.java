package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;

public class AppointmentDetailsDTO {

    private Long id;
    private ClientDTO client;
    private ProfessionalDTO professional;
    private ServiceDTO service;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private PaymentDTO payment;
    private String observations;
}
