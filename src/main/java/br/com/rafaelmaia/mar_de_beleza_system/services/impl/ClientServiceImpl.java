package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Client;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ClientRepository;
import br.com.rafaelmaia.mar_de_beleza_system.services.ClientService;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ClientRepository repository;

    public Client findClientById(Long id) {
        Optional<Client> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Client not found!"));
    }

    @Override
    public List<Client> findAllClients() {
        return repository.findAll();
    }

    @Override
    public Client createClient(ClientDTO obj) {
        return repository.save(mapper.map(obj, Client.class));
    }
}
