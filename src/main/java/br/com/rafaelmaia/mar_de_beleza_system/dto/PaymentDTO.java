package br.com.rafaelmaia.mar_de_beleza_system.dto;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDTO {

    private Long id;
    private BigDecimal amount;
    private BigDecimal discount;
    private LocalDateTime paymentDate;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
}
