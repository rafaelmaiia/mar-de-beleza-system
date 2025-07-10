package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientResponseDTO;

import java.util.List;

public interface ClientService {

    ClientResponseDTO findClientById(Long id);
    List<ClientResponseDTO> findAllClients();
    ClientResponseDTO createClient(ClientRequestDTO requestDTO);
    ClientResponseDTO updateClient(Long id, ClientRequestDTO requestDTO);
    void deleteClient(Long id);
}
