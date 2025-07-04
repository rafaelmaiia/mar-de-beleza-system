package br.com.rafaelmaia.mar_de_beleza_system.repository;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    @Query("SELECT a FROM Appointment a JOIN a.services item " +
            "WHERE item.professional.id = :professionalId " +
            "AND a.status <> br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus.CANCELED " +
            "AND a.appointmentDate >= :startOfDay " +
            "AND a.appointmentDate < :endOfDay")
    List<Appointment> findPotentialConflicts(Long professionalId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
