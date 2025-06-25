package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SalonService;
import br.com.rafaelmaia.mar_de_beleza_system.dto.SalonServiceDTO;

import java.util.List;

public interface SalonServiceService {

    SalonService findSalonServiceById(Long id);
    List<SalonService> findAllSalonServices();
    SalonService createSalonService(SalonServiceDTO obj);
    SalonService updateSalonService(SalonServiceDTO obj);
    void deleteSalonService(Long id);
}
