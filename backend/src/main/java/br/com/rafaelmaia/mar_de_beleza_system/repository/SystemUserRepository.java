package br.com.rafaelmaia.mar_de_beleza_system.repository;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {

    Optional<SystemUser> findByEmail(String email);
    Optional<SystemUser> findByContact_Phone(String phone);
}
