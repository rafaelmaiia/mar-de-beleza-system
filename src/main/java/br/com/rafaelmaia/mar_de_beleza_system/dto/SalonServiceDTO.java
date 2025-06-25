package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SalonService;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalonServiceDTO {

    private Long id;
    private String name;
    private ServiceType serviceType;

    public static SalonServiceDTO fromEntity(SalonService service) {
        if (service == null) {
            return null;
        }
        return SalonServiceDTO.builder()
                .id(service.getId())
                .name(service.getName())
                .serviceType(service.getServiceType() != null ? service.getServiceType() : null)
                .build();
    }
}
