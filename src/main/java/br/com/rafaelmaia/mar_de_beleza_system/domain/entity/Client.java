package br.com.rafaelmaia.mar_de_beleza_system.domain.entity;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_client")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender; // Use um enum para representar o gênero

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    public List<Appointment> getAppointmentHistory() {
        return this.appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.DONE)
                .collect(Collectors.toList());
    }
}
