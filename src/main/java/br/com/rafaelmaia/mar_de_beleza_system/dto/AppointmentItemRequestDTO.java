package br.com.rafaelmaia.mar_de_beleza_system.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentItemRequestDTO {

    private Long salonServiceId;
    private Long professionalId;
    private BigDecimal price;
}
