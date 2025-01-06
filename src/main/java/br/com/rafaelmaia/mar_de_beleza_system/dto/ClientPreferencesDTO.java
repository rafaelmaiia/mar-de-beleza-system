package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;

public class ClientPreferencesDTO {

    private Long id;
    private ClientDTO client;
    private ServiceType favoriteTypeService;
    private String observations;
}
