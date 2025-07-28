package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Client;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Contact;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ClientRepository;
import br.com.rafaelmaia.mar_de_beleza_system.services.ClientService;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.DataIntegrityViolationException;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;

    @Transactional(readOnly = true)
    public ClientResponseDTO findClientById(Long id) {
        Client clientEntity = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Cliente não encontrado com ID: " + id));

        return ClientResponseDTO.fromEntity(clientEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponseDTO> findAllClients(Pageable pageable) {
        Page<Client> clientPage = repository.findAll(pageable);

        return clientPage.map(ClientResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public ClientResponseDTO createClient(ClientRequestDTO requestDTO) {
        validatePhoneUniqueness(requestDTO.contact().phone(), null);

        Client newClient = new Client();
        newClient.setName(requestDTO.name());
        newClient.setBirthDate(requestDTO.birthDate());
        newClient.setGender(requestDTO.gender());

        Contact newContact = new Contact();
        newContact.setPhone(requestDTO.contact().phone());
        newContact.setPhoneIsWhatsapp(requestDTO.contact().phoneIsWhatsapp());
        newClient.setContact(newContact);

        Client savedClient = repository.save(newClient);

        return ClientResponseDTO.fromEntity(savedClient);
    }

    @Override
    @Transactional
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO requestDTO) {
        validatePhoneUniqueness(requestDTO.contact().phone(), id);

        Client clientToUpdate = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Cliente não encontrado com ID: " + id));

        clientToUpdate.setName(requestDTO.name());
        clientToUpdate.setBirthDate(requestDTO.birthDate());
        // Lógica para atualizar o contato se necessário
        clientToUpdate.getContact().setPhone(requestDTO.contact().phone());
        clientToUpdate.getContact().setPhoneIsWhatsapp(requestDTO.contact().phoneIsWhatsapp());

        Client updatedClient = repository.save(clientToUpdate);

        return ClientResponseDTO.fromEntity(updatedClient);
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        if (!repository.existsById(id)) {
            throw new ObjectNotFoundException("Cliente não encontrado com ID: " + id);
        }

        repository.deleteById(id);
    }

    // Validar unicidade de telefone
    private void validatePhoneUniqueness(String phone, Long idToIgnore) {
        Optional<Client> client = repository.findByContact_Phone(phone);

        if (client.isPresent() && !client.get().getId().equals(idToIgnore)) {
            throw new DataIntegrityViolationException("Telefone já cadastrado no sistema.");
        }
    }
}
