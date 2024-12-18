package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Client;

public interface ClientService {

    Client findClientById(Long id);
}
