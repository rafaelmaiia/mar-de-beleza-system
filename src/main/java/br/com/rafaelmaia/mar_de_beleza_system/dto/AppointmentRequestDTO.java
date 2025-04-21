package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDTO {

    @NotNull
    private Long clientId; // Apenas o ID do cliente

    @NotNull
    private ServiceType serviceName; // Tipo do serviço

    @NotNull
    private Long professionalId; // Apenas o ID do profissional

    @NotNull
    private LocalDateTime appointmentDate; // Data do agendamento

    private AppointmentStatus status; // Status do agendamento

    private String observations; // Observações
}
