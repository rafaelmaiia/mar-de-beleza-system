package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalResponseDTO;

import java.util.List;

public interface ProfessionalService {

    ProfessionalResponseDTO findProfessionalById(Long id);
    List<ProfessionalResponseDTO> findAllProfessionals();
    ProfessionalResponseDTO createProfessional(ProfessionalRequestDTO requestDTO);
    ProfessionalResponseDTO updateProfessional(Long id, ProfessionalRequestDTO requestDTO);
    void deleteProfessional(Long id);
}
