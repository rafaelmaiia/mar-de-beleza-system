package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    private Long id;
    private String name;
    private ContactDTO contact;

    public static ClientDTO fromEntity(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        return dto;
    }
}
