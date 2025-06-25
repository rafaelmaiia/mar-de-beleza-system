package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SalonService;
import br.com.rafaelmaia.mar_de_beleza_system.dto.SalonServiceDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.SalonServiceRepository;
import br.com.rafaelmaia.mar_de_beleza_system.services.SalonServiceService;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalonServiceServiceImpl implements SalonServiceService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private SalonServiceRepository repository;

    public SalonService findSalonServiceById(Long id) {
        Optional<SalonService> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Service not found!"));
    }

    @Override
    public List<SalonService> findAllSalonServices() {
        return repository.findAll();
    }

    @Override
    public SalonService createSalonService(SalonServiceDTO obj) {

        return repository.save(mapper.map(obj, SalonService.class));
    }

    @Override
    public SalonService updateSalonService(SalonServiceDTO obj) {

        return repository.save(mapper.map(obj, SalonService.class));
    }

    @Override
    public void deleteSalonService(Long id) {
        findSalonServiceById(id);
        repository.deleteById(id);
    }
}
