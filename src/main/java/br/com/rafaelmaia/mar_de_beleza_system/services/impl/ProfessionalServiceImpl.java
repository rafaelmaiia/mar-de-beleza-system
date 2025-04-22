package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Professional;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ProfessionalRepository;
import br.com.rafaelmaia.mar_de_beleza_system.services.ProfessionalService;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessionalServiceImpl implements ProfessionalService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ProfessionalRepository repository;

    public Professional findProfessionalById(Long id) {
        Optional<Professional> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Professional not found!"));
    }

    @Override
    public List<Professional> findAllProfessionals() {
        return repository.findAll();
    }

    @Override
    public Professional createProfessional(ProfessionalDTO obj) {
        return repository.save(mapper.map(obj, Professional.class));
    }

    @Override
    public Professional updateProfessional(ProfessionalDTO obj) {
        return repository.save(mapper.map(obj, Professional.class));
    }

    @Override
    public void deleteProfessional(Long id) {
        findProfessionalById(id);
        repository.deleteById(id);
    }
}
