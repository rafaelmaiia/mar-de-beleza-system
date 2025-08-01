package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Appointment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Payment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentMethod;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.PaymentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.dto.PaymentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.PaymentResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.AppointmentRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.PaymentRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.specification.PaymentSpecification;
import br.com.rafaelmaia.mar_de_beleza_system.services.PaymentService;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.BusinessRuleException;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO findPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Pagamento não encontrado com id:" + id));

        return PaymentResponseDTO.fromEntity(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> findAllPayments(LocalDate startDate, LocalDate endDate, Long professionalId, PaymentStatus status,Pageable pageable) {
        Specification<Payment> spec = PaymentSpecification.withFilters(startDate, endDate, professionalId, status);

        Page<Payment> paymentPage = paymentRepository.findAll(spec, pageable);

        return paymentPage.map(PaymentResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public PaymentResponseDTO create(PaymentRequestDTO request) {
        logger.info("Iniciando processo de criação de Pagamento para o Agendamento ID: {}", request.appointmentId());

        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new ObjectNotFoundException("Agendamento não encontrado com id " + request.appointmentId()));

        // Regra de negócio: Não permite registrar pagamento para agendamentos que não sejam "Agendado" ou "Confirmado"
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED && appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BusinessRuleException(
                    "Apenas agendamentos com status 'Agendado' ou 'Confirmado' podem ser concluídos e ter um pagamento registrado."
            );
        }

        Payment newPayment = Payment.builder()
                .appointment(appointment)
                .totalAmount(request.totalAmount())
                .paymentMethod(request.paymentMethod())
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.PAID)
                .observations(request.observations()).build();

        Payment savedPayment = paymentRepository.save(newPayment);

        // Após salvar o pagamento, atualiza o status do agendamento
        appointment.setStatus(AppointmentStatus.DONE);
        appointmentRepository.save(appointment);
        logger.info("Status do Agendamento ID {} atualizado para DONE.", appointment.getId());

        logger.info("Pagamento ID {} criado com sucesso.", savedPayment.getId());
        return PaymentResponseDTO.fromEntity(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponseDTO update(Long id, PaymentRequestDTO request) {
        logger.info("Iniciando processo de atualização para o Pagamento ID: {}", id);

        Payment paymentToUpdate = paymentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Pagamento não encontrado com id:" + id));

        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new ObjectNotFoundException("Agendamento não encontrado com id " + request.appointmentId()));

        paymentToUpdate.setAppointment(appointment);
        paymentToUpdate.setTotalAmount(request.totalAmount());
        paymentToUpdate.setPaymentMethod(request.paymentMethod());
        paymentToUpdate.setObservations(request.observations());

        Payment updatedPayment = paymentRepository.save(paymentToUpdate);
        logger.info("Agendamento ID {} atualizado com sucesso.", updatedPayment.getId());

        return PaymentResponseDTO.fromEntity(updatedPayment);
    }

    @Override
    @Transactional
    public void cancelPayment(Long id) {
        logger.info("Iniciando cancelamento do pagamento ID: {}", id);

        Payment paymentToCancel = paymentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Pagamento não encontrado com id:" + id));

        // Regra de negócio: Não permite cancelar um pagamento que já está cancelado
        if (paymentToCancel.getStatus() == PaymentStatus.CANCELED) {
            throw new BusinessRuleException("Este pagamento já foi cancelado.");
        }

        paymentToCancel.setStatus(PaymentStatus.CANCELED);
        paymentRepository.save(paymentToCancel);

        logger.info("Pagamento ID {} cancelado com sucesso.", id);
    }
}
