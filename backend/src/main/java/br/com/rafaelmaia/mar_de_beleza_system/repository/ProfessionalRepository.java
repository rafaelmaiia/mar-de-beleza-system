package br.com.rafaelmaia.mar_de_beleza_system.repository;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Professional;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    Optional<Professional> findByContact_Phone(String phone);

    List<Professional> findBySpecialtiesContaining(ServiceType specialty);
}
