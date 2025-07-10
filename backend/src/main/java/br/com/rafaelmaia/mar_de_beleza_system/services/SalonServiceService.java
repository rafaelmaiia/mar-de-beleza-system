package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.dto.SalonServiceRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.SalonServiceResponseDTO;

import java.util.List;

public interface SalonServiceService {

    SalonServiceResponseDTO findServiceById(Long id);
    List<SalonServiceResponseDTO> findAllServices();
    SalonServiceResponseDTO createService(SalonServiceRequestDTO requestDTO);
    SalonServiceResponseDTO updateService(Long id, SalonServiceRequestDTO requestDTO);
    void deleteService(Long id);
}
