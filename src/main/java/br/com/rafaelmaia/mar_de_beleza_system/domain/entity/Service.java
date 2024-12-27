package br.com.rafaelmaia.mar_de_beleza_system.domain.entity;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.EyebrowType;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.LashType;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
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

    private String name;
    private String description;
    private ServiceType serviceType;
    private LashType lashType;
    private EyebrowType eyebrowType;
    private List<Professional> professionals;
}
