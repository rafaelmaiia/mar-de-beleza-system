package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SalonService;
import br.com.rafaelmaia.mar_de_beleza_system.dto.SalonServiceRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.SalonServiceResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.AppointmentRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.SalonServiceRepository;
import br.com.rafaelmaia.mar_de_beleza_system.services.SalonServiceService;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.DataIntegrityViolationException;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalonServiceServiceImpl implements SalonServiceService {

    private final SalonServiceRepository repository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional(readOnly = true)
    public SalonServiceResponseDTO findServiceById(Long id) {
        SalonService service = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Serviço não encontrado com ID: " + id));
        return SalonServiceResponseDTO.fromEntity(service);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalonServiceResponseDTO> findAllServices() {
        return repository.findAll().stream()
                .map(SalonServiceResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SalonServiceResponseDTO createService(SalonServiceRequestDTO requestDTO) {
        SalonService newService = new SalonService();
        newService.setName(requestDTO.name());
        newService.setServiceType(requestDTO.serviceType());
        newService.setDurationInMinutes(requestDTO.durationInMinutes());
        newService.setPrice(requestDTO.price());

        SalonService savedService = repository.save(newService);
        return SalonServiceResponseDTO.fromEntity(savedService);
    }

    @Override
    @Transactional
    public SalonServiceResponseDTO updateService(Long id, SalonServiceRequestDTO requestDTO) {
        SalonService serviceToUpdate = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Serviço não encontrado com ID: " + id));

        serviceToUpdate.setName(requestDTO.name());
        serviceToUpdate.setServiceType(requestDTO.serviceType());
        serviceToUpdate.setDurationInMinutes(requestDTO.durationInMinutes());
        serviceToUpdate.setPrice(requestDTO.price());

        SalonService updatedService = repository.save(serviceToUpdate);
        return SalonServiceResponseDTO.fromEntity(updatedService);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        if (!repository.existsById(id)) {
            throw new ObjectNotFoundException("Serviço não encontrado com ID: " + id);
        }

        // Verifica se algum agendamento está usando este serviço
        if (appointmentRepository.existsByServiceId(id)) {
            throw new DataIntegrityViolationException(
                    "Este serviço não pode ser excluído pois já está vinculado a um ou mais agendamentos."
            );
        }

        repository.deleteById(id);
    }
}
