package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.Role;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Set;

public record UserRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio")
        String name,

        @NotBlank(message = "O email não pode ser vazio")
        @Email(message = "Formato de email inválido")
        String email,

        // A senha só é obrigatória na criação, podemos ajustar a lógica no serviço
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password,

        @NotNull(message = "A role (perfil) é obrigatória")
        Role role,

        @NotNull(message = "Os dados de contato são obrigatórios")
        @Valid
        ContactRequestDTO contact,

        @NotEmpty(message = "O profissional deve ter pelo menos uma especialidade")
        Set<ServiceType> specialties
) {}
