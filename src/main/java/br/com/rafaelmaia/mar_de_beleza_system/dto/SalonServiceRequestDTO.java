package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SalonServiceRequestDTO(
        @NotBlank(message = "O nome do serviço não pode ser vazio")
        String name,

        @NotNull(message = "O tipo do serviço é obrigatório")
        ServiceType serviceType,

        @NotNull(message = "A duração do serviço é obrigatória")
        @Positive(message = "A duração deve ser um número positivo de minutos")
        Integer durationInMinutes,

        @NotNull(message = "O preço do serviço é obrigatório")
        @Positive(message = "O preço deve ser um valor positivo")
        BigDecimal price
) {}
