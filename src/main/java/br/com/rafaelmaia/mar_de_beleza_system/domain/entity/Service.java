package br.com.rafaelmaia.mar_de_beleza_system.domain.entity;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.EyebrowType;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.LashType;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_service")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    private LashType lashType; // Só faz sentido se o serviceType for LASH

    @Enumerated(EnumType.STRING)
    private EyebrowType eyebrowType; // Só faz sentido se o serviceType for EYEBROW

    @ManyToMany
    @JoinTable(name = "service_professional",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "professional_id"))
    private List<Professional> professionals = new ArrayList<>();

    // Validação para garantir coerência entre ServiceType e os tipos específicos
    @PrePersist
    @PreUpdate
    private void validateServiceType() {
        if (serviceType == ServiceType.LASH && lashType == null) {
            throw new IllegalArgumentException("LashType deve ser preenchido para serviços do tipo CILIOS.");
        }
        if (serviceType == ServiceType.EYEBROW && eyebrowType == null) {
            throw new IllegalArgumentException("EyebrowType deve ser preenchido para serviços do tipo SOBRANCELHAS.");
        }
        if (serviceType != ServiceType.LASH) {
            lashType = null; // Remove valores desnecessários
        }
        if (serviceType != ServiceType.EYEBROW) {
            eyebrowType = null; // Remove valores desnecessários
        }
    }
}
