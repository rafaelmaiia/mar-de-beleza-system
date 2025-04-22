package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Professional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionalDTO {

    private Long id;
    private String name;
    private ContactDTO contact;

    public static ProfessionalDTO fromEntity(Professional professional) {
        ProfessionalDTO dto = new ProfessionalDTO();
        dto.setId(professional.getId());
        dto.setName(professional.getName());
        return dto;
    }
}
