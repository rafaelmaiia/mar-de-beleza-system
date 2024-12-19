package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Client;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientDTO;

import java.util.List;

public interface ClientService {

    Client findClientById(Long id);
    List<Client> findAllClients();
    Client createClient(ClientDTO obj);
    Client updateClient(ClientDTO obj);
}
