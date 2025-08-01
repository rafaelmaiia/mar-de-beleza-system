package br.com.rafaelmaia.mar_de_beleza_system.repository.specification;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Appointment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Payment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SystemUser;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public class PaymentSpecification {

    // Metodo principal que combina os filtros (Pagina Financeiro)
    public static Specification<Payment> withFilters(
            LocalDate startDate, LocalDate endDate, Long professionalId, PaymentStatus status) {

        return Specification.where(byProfessional(professionalId))
                .and(byDateRange(startDate, endDate))
                .and(byStatus(status));
    }

    // metodo para o intervalo de datas
    private static Specification<Payment> byDateRange(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate != null && endDate != null) {
                return cb.between(root.get("paymentDate"), startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
            }
            if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("paymentDate"), startDate.atStartOfDay());
            }
            if (endDate != null) {
                return cb.lessThanOrEqualTo(root.get("paymentDate"), endDate.atTime(LocalTime.MAX));
            }
            return null;
        };
    }

    // metodo para filtrar por status
    private static Specification<Payment> byStatus(PaymentStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<Payment> byProfessional(Long professionalId) {
        return (root, query, cb) -> {
            if (professionalId == null) {
                return null;
            }
            // Faz um JOIN da raiz (Payment) para o campo 'appointment'
            Join<Payment, Appointment> appointmentJoin = root.join("appointment");
            // A partir do JOIN de appointment, pegamos o 'professional'
            Join<Appointment, SystemUser> professionalJoin = appointmentJoin.join("professional");
            // Comparamos o ID
            return cb.equal(professionalJoin.get("id"), professionalId);
        };
    }
}
