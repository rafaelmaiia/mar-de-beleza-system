package br.com.rafaelmaia.mar_de_beleza_system.domain.entity;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_appointment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Client client;
    private Service service;
    private Professional professional;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private Payment payment;
    private ClientPreferences clientPreferences;
    private String observations;
    private LocalDate createdAt;
    private LocalDateTime nextAppointment;
}
