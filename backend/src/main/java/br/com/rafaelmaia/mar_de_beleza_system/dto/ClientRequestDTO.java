package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ClientRequestDTO(
        @NotBlank(message = "O nome não pode ser em branco")
        String name,

        @NotNull(message = "O contato não pode ser nulo")
        @Valid
        ContactRequestDTO contact,

        LocalDate birthDate,
        Gender gender
) {}
