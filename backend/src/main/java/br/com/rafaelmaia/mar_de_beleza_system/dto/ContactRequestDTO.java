package br.com.rafaelmaia.mar_de_beleza_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ContactRequestDTO(
        @NotBlank(message = "O número de telefone não pode ser vazio")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "O telefone deve conter apenas números e ter entre 10 e 11 dígitos")
        String phone,

        @NotNull(message = "É necessário informar se o telefone é WhatsApp ou não")
        Boolean phoneIsWhatsapp
) {}
