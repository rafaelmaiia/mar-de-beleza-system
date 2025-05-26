package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContactDTO {

    private Long id;
    private String phone;
    private Boolean phoneIsWhatsapp;

    public static ContactDTO fromEntity(Contact contact) {
        if (contact == null) return null;

        ContactDTO dto = new ContactDTO();
        dto.setId(contact.getId());
        dto.setPhone(contact.getPhone());
        dto.setPhoneIsWhatsapp(contact.getPhoneIsWhatsapp());
        return dto;
    }
}
