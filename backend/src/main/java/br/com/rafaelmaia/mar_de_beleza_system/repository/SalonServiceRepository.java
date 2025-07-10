package br.com.rafaelmaia.mar_de_beleza_system.repository;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {
}
