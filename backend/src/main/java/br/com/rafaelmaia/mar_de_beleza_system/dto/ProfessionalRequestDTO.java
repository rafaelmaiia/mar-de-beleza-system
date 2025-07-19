package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record ProfessionalRequestDTO(
        @NotBlank(message = "O nome do profissional não pode ser vazio")
        String name,

        @NotNull(message = "Os dados de contato são obrigatórios")
        @Valid
        ContactRequestDTO contact,
        
        @NotEmpty(message = "O profissional deve ter pelo menos uma especialidade")
        Set<ServiceType> specialties
) {}
