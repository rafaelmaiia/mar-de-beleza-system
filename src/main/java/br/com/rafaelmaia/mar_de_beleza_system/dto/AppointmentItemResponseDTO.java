package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.AppointmentItem;

import java.math.BigDecimal;


public record AppointmentItemResponseDTO(
        Long id,
        SalonServiceResponseDTO service,
        ProfessionalResponseDTO professional,
        BigDecimal price
) {

    public static AppointmentItemResponseDTO fromEntity(AppointmentItem item) {
        if (item == null) {
            return null;
        }

        return new AppointmentItemResponseDTO(
                item.getId(),
                SalonServiceResponseDTO.fromEntity(item.getService()),
                ProfessionalResponseDTO.fromEntity(item.getProfessional()),
                item.getPrice()
        );
    }
}
