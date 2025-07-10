package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Client;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.Gender;

import java.time.LocalDate;

public record ClientResponseDTO(
        Long id,
        String name,
        ContactResponseDTO contact,
        LocalDate birthDate,
        Gender gender
) {
    public static ClientResponseDTO fromEntity(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                ContactResponseDTO.fromEntity(client.getContact()),
                client.getBirthDate(),
                client.getGender()
        );
    }
}
