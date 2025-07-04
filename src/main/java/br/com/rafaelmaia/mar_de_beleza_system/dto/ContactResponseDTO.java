package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Contact;

public record ContactResponseDTO(
        Long id,
        String phone,
        Boolean phoneIsWhatsapp
) {
    public static ContactResponseDTO fromEntity(Contact contact) {
        if (contact == null) {
            return null;
        }
        return new ContactResponseDTO(
                contact.getId(),
                contact.getPhone(),
                contact.getPhoneIsWhatsapp()
        );
    }
}
