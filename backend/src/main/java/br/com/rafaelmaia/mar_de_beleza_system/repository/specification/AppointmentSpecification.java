package br.com.rafaelmaia.mar_de_beleza_system.repository.specification;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Appointment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentSpecification {

    // Este metodo é para o DASHBOARD (busca por um único dia)
    public static Specification<Appointment> byDate(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) return null;
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            return cb.between(root.get("appointmentDate"), startOfDay, endOfDay);
        };
    }

    // Este metodo é para a PÁGINA DE GERENCIAMENTO (busca por múltiplos filtros)
    public static Specification<Appointment> withFilters(
            LocalDate startDate, LocalDate endDate, Long professionalId, Long clientId, String status) {

        return Specification.where(byProfessional(professionalId))
                .and(byClient(clientId))
                .and(byStatus(status))
                .and(byDateRange(startDate, endDate));
    }

    // Metodo privado para o intervalo de datas
    private static Specification<Appointment> byDateRange(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate != null && endDate != null) {
                return cb.between(root.get("appointmentDate"), startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
            }
            if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("appointmentDate"), startDate.atStartOfDay());
            }
            if (endDate != null) {
                return cb.lessThanOrEqualTo(root.get("appointmentDate"), endDate.atTime(LocalTime.MAX));
            }
            return null;
        };
    }

    private static Specification<Appointment> byProfessional(Long professionalId) {
        return (root, query, criteriaBuilder) -> {
            if (professionalId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("professional").get("id"), professionalId);
        };
    }

    private static Specification<Appointment> byClient(Long clientId) {
        return (root, query, criteriaBuilder) ->
                clientId == null ? null : criteriaBuilder.equal(root.get("client").get("id"), clientId);
    }

    // Metodo para filtrar por status
    private static Specification<Appointment> byStatus(String status) {
        // Checa se a string é nula ou vazia
        return (root, query, cb) -> {
            if (!StringUtils.hasText(status)) {
                return null;
            }
            try {
                // Converte a string para Enum, garantindo que seja um valor válido
                AppointmentStatus statusEnum = AppointmentStatus.valueOf(status);
                return cb.equal(root.get("status"), statusEnum);
            } catch (IllegalArgumentException e) {
                // Se o status enviado for inválido (ex: "QUALQUERCOISA"), ignora o filtro
                return null;
            }
        };
    }
}