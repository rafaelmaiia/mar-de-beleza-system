package br.com.rafaelmaia.mar_de_beleza_system.domain.entity;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_salon_service")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SalonService implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name; // Nome comercial do serviço

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType; // Categoria

    @Column(nullable = false)
    private Integer durationInMinutes; // Duração do serviço

    @Column(nullable = false)
    private BigDecimal price;
}
