package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Professional;

public record ProfessionalResponseDTO(
        Long id,
        String name,
        ContactResponseDTO contact
) {
    public static ProfessionalResponseDTO fromEntity(Professional professional) {
        if (professional == null) {
            return null;
        }
        return new ProfessionalResponseDTO(
                professional.getId(),
                professional.getName(),
                ContactResponseDTO.fromEntity(professional.getContact())
        );
    }
}
