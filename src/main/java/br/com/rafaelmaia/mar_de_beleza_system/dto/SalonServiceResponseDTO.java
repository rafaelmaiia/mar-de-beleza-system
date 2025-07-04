package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SalonService;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;

import java.math.BigDecimal;

public record SalonServiceResponseDTO(
        Long id,
        String name,
        ServiceType serviceType,
        Integer durationInMinutes,
        BigDecimal price
) {
    public static SalonServiceResponseDTO fromEntity(SalonService service) {
        if (service == null) {
            return null;
        }
        return new SalonServiceResponseDTO(
                service.getId(),
                service.getName(),
                service.getServiceType(),
                service.getDurationInMinutes(),
                service.getPrice()
        );
    }
}
