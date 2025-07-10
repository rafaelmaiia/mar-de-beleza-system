package br.com.rafaelmaia.mar_de_beleza_system.repository.specification;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Appointment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.AppointmentItem;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentSpecification {

    public static Specification<Appointment> withFilters(LocalDate date, Long professionalId, Long clientId) {
        // Começa com uma especificação que não faz nada e adiciona as outras com 'and'
        return Specification.where(hasDate(date))
                .and(hasProfessional(professionalId))
                .and(hasClient(clientId));
    }

    private static Specification<Appointment> hasDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return null; // Ignora o filtro se a data for nula
            }
            // Define o início do dia (ex: 2025-07-03T00:00:00)
            LocalDateTime startOfDay = date.atStartOfDay();
            // Define o início do dia seguinte (ex: 2025-07-04T00:00:00)
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            // A condição agora é: appointmentDate >= startOfDay AND appointmentDate < endOfDay
            return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("appointmentDate"), startOfDay),
                    criteriaBuilder.lessThan(root.get("appointmentDate"), endOfDay)
            );
        };
    }

    private static Specification<Appointment> hasProfessional(Long professionalId) {
        return (root, query, criteriaBuilder) -> {
            if (professionalId == null) {
                return null; // Ignora o filtro
            }
            // Faz um JOIN com a lista de services (AppointmentItem)
            Join<Appointment, AppointmentItem> appointmentItemJoin = root.join("services");
            // Filtra pelo ID do profissional dentro do item do agendamento
            return criteriaBuilder.equal(appointmentItemJoin.get("professional").get("id"), professionalId);
        };
    }

    private static Specification<Appointment> hasClient(Long clientId) {
        return (root, query, criteriaBuilder) ->
                clientId == null ? null : criteriaBuilder.equal(root.get("client").get("id"), clientId);
    }
}