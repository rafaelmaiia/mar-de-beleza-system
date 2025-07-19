package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Contact;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Professional;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ProfessionalRepository;
import br.com.rafaelmaia.mar_de_beleza_system.services.ProfessionalService;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.DataIntegrityViolationException;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionalServiceImpl implements ProfessionalService {

    private final ProfessionalRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(ProfessionalServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public ProfessionalResponseDTO findProfessionalById(Long id) {
        Professional professional = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Profissional não encontrado com ID: " + id));
        return ProfessionalResponseDTO.fromEntity(professional);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessionalResponseDTO> findAllProfessionals(ServiceType specialty) {
        List<Professional> professionalList;
        if (specialty != null) {
            professionalList = repository.findBySpecialtiesContaining(specialty);
        } else {
            professionalList = repository.findAll();
        }
        return professionalList.stream()
                .map(ProfessionalResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProfessionalResponseDTO createProfessional(ProfessionalRequestDTO requestDTO) {
        logger.info("Iniciando criação de novo profissional: {}", requestDTO.name());

        validatePhoneUniqueness(requestDTO.contact().phone(), null);

        Professional newProfessional = new Professional();
        newProfessional.setName(requestDTO.name());

        Contact newContact = new Contact();
        newContact.setPhone(requestDTO.contact().phone());
        newContact.setPhoneIsWhatsapp(requestDTO.contact().phoneIsWhatsapp());
        newProfessional.setContact(newContact);
        newProfessional.setSpecialties(requestDTO.specialties());

        Professional savedProfessional = repository.save(newProfessional);
        logger.info("Profissional ID {} criado com sucesso.", savedProfessional.getId());

        return ProfessionalResponseDTO.fromEntity(savedProfessional);
    }

    @Override
    @Transactional
    public ProfessionalResponseDTO updateProfessional(Long id, ProfessionalRequestDTO requestDTO) {
        logger.info("Iniciando atualização para o profissional ID: {}", id);

        validatePhoneUniqueness(requestDTO.contact().phone(), id);

        Professional professionalToUpdate = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Profissional não encontrado com ID: " + id));

        professionalToUpdate.setName(requestDTO.name());
        professionalToUpdate.getContact().setPhone(requestDTO.contact().phone());
        professionalToUpdate.getContact().setPhoneIsWhatsapp(requestDTO.contact().phoneIsWhatsapp());
        professionalToUpdate.setSpecialties(requestDTO.specialties());

        Professional updatedProfessional = repository.save(professionalToUpdate);
        logger.info("Profissional ID {} atualizado com sucesso.", updatedProfessional.getId());

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

    private void validatePhoneUniqueness(String phone, Long idToIgnore) {
        Optional<Professional> professional = repository.findByContact_Phone(phone);
        if (professional.isPresent() && !professional.get().getId().equals(idToIgnore)) {
            throw new DataIntegrityViolationException("Telefone já cadastrado no sistema.");
        }
    }
}
