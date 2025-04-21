package br.com.rafaelmaia.mar_de_beleza_system.repository;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
}
