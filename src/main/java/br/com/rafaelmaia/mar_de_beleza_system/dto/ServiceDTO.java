package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;

import java.util.List;

public class ServiceDTO {

    private Long id;
    private String name;
    private String description;
    private ServiceType serviceType; // Lash, Eyebrow, etc.
    private List<ProfessionalDTO> professionals; // Lista de profissionais associados

}
