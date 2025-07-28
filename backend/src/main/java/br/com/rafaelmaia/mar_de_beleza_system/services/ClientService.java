package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    ClientResponseDTO findClientById(Long id);
    Page<ClientResponseDTO> findAllClients(Pageable pageable);
    ClientResponseDTO createClient(ClientRequestDTO requestDTO);
    ClientResponseDTO updateClient(Long id, ClientRequestDTO requestDTO);
    void deleteClient(Long id);
}
