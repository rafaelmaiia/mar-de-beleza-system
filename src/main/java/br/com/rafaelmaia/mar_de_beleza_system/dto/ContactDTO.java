package br.com.rafaelmaia.mar_de_beleza_system.dto;

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
}
