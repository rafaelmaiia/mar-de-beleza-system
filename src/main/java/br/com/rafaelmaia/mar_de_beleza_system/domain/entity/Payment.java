package br.com.rafaelmaia.mar_de_beleza_system.domain.entity;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private BigDecimal discount;
    private LocalDateTime paymentDate;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private Appointment appointment;
    private Professional professional;
    private Client client;
    private Service service;
}
