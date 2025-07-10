package br.com.rafaelmaia.mar_de_beleza_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfessionalRequestDTO(
        @NotBlank(message = "O nome do profissional não pode ser vazio")
        String name,

        @NotNull(message = "Os dados de contato são obrigatórios")
        @Valid
        ContactRequestDTO contact
) {}
