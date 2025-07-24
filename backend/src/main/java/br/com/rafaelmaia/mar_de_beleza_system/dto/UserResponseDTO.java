package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SystemUser;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.Role;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;

import java.util.Set;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Role role,
        ContactResponseDTO contact,
        Set<ServiceType> specialties
) {
    public static UserResponseDTO fromEntity(SystemUser user) {
        if (user == null) return null;
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                ContactResponseDTO.fromEntity(user.getContact()),
                user.getSpecialties()
        );
    }
}
