package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.AppointmentItem;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentItemDTO {

    private Long id;
    private SalonServiceDTO service;
    private ProfessionalDTO professional;
    private BigDecimal price;

    public static AppointmentItemDTO fromEntity(AppointmentItem item) {
        if (item == null) {
            return null;
        }
        return AppointmentItemDTO.builder()
                .id(item.getId())
                .service(SalonServiceDTO.fromEntity(item.getService()))
                .professional(ProfessionalDTO.fromEntity(item.getProfessional()))
                .price(item.getPrice())
                .build();
    }
}
