package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Professional;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalDTO;

import java.util.List;

public interface ProfessionalService {

    Professional findProfessionalById(Long id);
    List<Professional> findAllProfessionals();
    Professional createProfessional(ProfessionalDTO obj);
    Professional updateProfessional(ProfessionalDTO obj);
    void deleteProfessional(Long id);
}
