package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Contact;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Professional;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ProfessionalRepository;
import br.com.rafaelmaia.mar_de_beleza_system.services.ProfessionalService;
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
public class ProfessionalServiceImpl implements ProfessionalService {

    private final ProfessionalRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ProfessionalResponseDTO findProfessionalById(Long id) {
        Professional professional = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Profissional não encontrado com ID: " + id));
        return ProfessionalResponseDTO.fromEntity(professional);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessionalResponseDTO> findAllProfessionals() {
        return repository.findAll().stream()
                .map(ProfessionalResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProfessionalResponseDTO createProfessional(ProfessionalRequestDTO requestDTO) {
        Professional newProfessional = new Professional();
        newProfessional.setName(requestDTO.name());

        Contact newContact = new Contact();
        newContact.setPhone(requestDTO.contact().phone());
        newContact.setPhoneIsWhatsapp(requestDTO.contact().phoneIsWhatsapp());
        newProfessional.setContact(newContact);

        Professional savedProfessional = repository.save(newProfessional);
        return ProfessionalResponseDTO.fromEntity(savedProfessional);
    }

    @Override
    @Transactional
    public ProfessionalResponseDTO updateProfessional(Long id, ProfessionalRequestDTO requestDTO) {
        Professional professionalToUpdate = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Profissional não encontrado com ID: " + id));

        professionalToUpdate.setName(requestDTO.name());
        professionalToUpdate.getContact().setPhone(requestDTO.contact().phone());
        professionalToUpdate.getContact().setPhoneIsWhatsapp(requestDTO.contact().phoneIsWhatsapp());

        Professional updatedProfessional = repository.save(professionalToUpdate);
        return ProfessionalResponseDTO.fromEntity(updatedProfessional);
    }

    @Override
    @Transactional
    public void deleteProfessional(Long id) {
        if (!repository.existsById(id)) {
            throw new ObjectNotFoundException("Profissional não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
