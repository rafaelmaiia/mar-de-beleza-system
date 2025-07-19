package br.com.rafaelmaia.mar_de_beleza_system.domain.entity;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_professional")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Professional implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ElementCollection(targetClass = ServiceType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "tb_professional_specialties", joinColumns = @JoinColumn(name = "professional_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "specialty", nullable = false)
    private Set<ServiceType> specialties = new HashSet<>();
}
