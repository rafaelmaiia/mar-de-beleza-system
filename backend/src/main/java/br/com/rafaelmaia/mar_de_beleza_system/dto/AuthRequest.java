package br.com.rafaelmaia.mar_de_beleza_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "O email não pode estar em branco")
        @Email(message = "O formato do email é inválido")
        String email,

        @NotBlank(message = "A senha não pode estar em branco")
        String password) {}

