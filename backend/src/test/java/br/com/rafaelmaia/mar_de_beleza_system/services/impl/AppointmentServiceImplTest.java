package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Appointment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Client;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Professional;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SalonService;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.AppointmentRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ClientRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ProfessionalRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.SalonServiceRepository;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    // Mocks de cada repositório
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ProfessionalRepository professionalRepository;
    @Mock
    private SalonServiceRepository salonServiceRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    // --- OBJETOS DE TESTE ---
    private Client client;
    private Professional professional;
    private SalonService salonService;
    private AppointmentRequestDTO requestDTO;

    @BeforeEach
    void setup() {
        // Arrange: cria os dados de teste com base no padrão AAA (Arrange-Act-Assert)
        client = new Client();
        client.setId(1L);
        client.setName("Cliente Teste");

        professional = new Professional();
        professional.setId(1L);
        professional.setName("Profissional Teste");

        salonService = new SalonService();
        salonService.setId(1L);
        salonService.setName("Serviço Teste");
        salonService.setDurationInMinutes(60);
        salonService.setPrice(new BigDecimal("100.00"));

        requestDTO = new AppointmentRequestDTO(
                client.getId(),
                LocalDateTime.of(2025, 8, 10, 10, 0), // Uma data futura
                salonService.getId(),
                professional.getId(),
                new BigDecimal("100.00"),
                "Observação de teste",
                AppointmentStatus.SCHEDULED
        );
    }

    @Test
    void givenNoConflicts_whenCreateAppointment_thenAppointmentShouldBeCreatedSuccessfully() {
        given(appointmentRepository.findPotentialConflicts(any(), any(), any())).willReturn(Collections.emptyList());

        given(clientRepository.findById(client.getId())).willReturn(Optional.of(client));
        given(professionalRepository.findById(professional.getId())).willReturn(Optional.of(professional));
        given(salonServiceRepository.findById(salonService.getId())).willReturn(Optional.of(salonService));

        given(appointmentRepository.save(any(Appointment.class))).willAnswer(invocation -> {
            Appointment appointmentToSave = invocation.getArgument(0);
            appointmentToSave.setId(10L);
            return appointmentToSave;
        });

        AppointmentResponseDTO createdAppointment = appointmentService.create(requestDTO);

        assertThat(createdAppointment).isNotNull();
        assertThat(createdAppointment.id()).isEqualTo(10L);
        assertThat(createdAppointment.client().name()).isEqualTo("Cliente Teste");
        assertThat(createdAppointment.status()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void givenExistingConflict_whenCreateAppointment_thenShouldThrowBusinessRuleException() {
        // Criando um agendamento FALSO que já existe no banco
        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentDate(LocalDateTime.of(2025, 8, 10, 9, 30));
        SalonService existingService = new SalonService();
        existingService.setDurationInMinutes(60); // Termina às 10:30, conflitando com o novo agendamento das 10:00
        existingAppointment.setService(existingService);

        given(clientRepository.findById(anyLong())).willReturn(Optional.of(client));
        given(professionalRepository.findById(anyLong())).willReturn(Optional.of(professional));
        given(salonServiceRepository.findById(anyLong())).willReturn(Optional.of(salonService));
        given(appointmentRepository.findPotentialConflicts(any(), any(), any())).willReturn(List.of(existingAppointment));

        // Ao tentar executar o appointmentService.create uma BusinessRuleException é lançada
        assertThatThrownBy(() -> appointmentService.create(requestDTO))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("Conflito de horário");

        // Garante que o fluxo foi interrompido e nenhum agendamento foi salvo
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void givenValidChanges_whenUpdateAppointment_thenShouldReturnUpdatedAppointment() {
        LocalDateTime newDate = LocalDateTime.of(2025, 8, 15, 14, 0);
        AppointmentRequestDTO updateRequest = new AppointmentRequestDTO(
                client.getId(), newDate, salonService.getId(), professional.getId(),
                new BigDecimal("120.00"), "Observação ATUALIZADA", AppointmentStatus.CONFIRMED
        );

        Appointment appointmentToUpdate = new Appointment();
        appointmentToUpdate.setId(10L);

        given(appointmentRepository.findById(10L)).willReturn(Optional.of(appointmentToUpdate));
        given(clientRepository.findById(anyLong())).willReturn(Optional.of(client));
        given(professionalRepository.findById(anyLong())).willReturn(Optional.of(professional));
        given(salonServiceRepository.findById(anyLong())).willReturn(Optional.of(salonService));
        given(appointmentRepository.findPotentialConflicts(any(), any(), any())).willReturn(Collections.emptyList());
        given(appointmentRepository.save(any(Appointment.class))).willAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponseDTO updatedAppointment = appointmentService.update(10L, updateRequest);

        assertThat(updatedAppointment).isNotNull();
        assertThat(updatedAppointment.id()).isEqualTo(10L);
        assertThat(updatedAppointment.appointmentDate()).isEqualTo(newDate);
        assertThat(updatedAppointment.observations()).isEqualTo("Observação ATUALIZADA");
    }

    @Test
    void givenConflictWithAnotherAppointment_whenUpdateAppointment_thenShouldThrowBusinessRuleException() {
        Appointment appointmentToUpdate = new Appointment();
        appointmentToUpdate.setId(10L);

        Appointment conflictingAppointment = new Appointment();
        conflictingAppointment.setId(11L);
        conflictingAppointment.setAppointmentDate(LocalDateTime.of(2025, 8, 8, 14, 0));
        SalonService existingService = new SalonService();
        existingService.setDurationInMinutes(60);
        conflictingAppointment.setService(existingService);
        conflictingAppointment.setProfessional(professional);

        AppointmentRequestDTO updateRequestWithConflict = new AppointmentRequestDTO(
                client.getId(),
                LocalDateTime.of(2025, 8, 8, 14, 30), // Tenta marcar em cima do conflito
                salonService.getId(),
                professional.getId(),
                new BigDecimal("150.00"),
                "Isso deve falhar",
                AppointmentStatus.SCHEDULED
        );

        given(appointmentRepository.findById(10L)).willReturn(Optional.of(appointmentToUpdate));
        given(clientRepository.findById(anyLong())).willReturn(Optional.of(client));
        given(professionalRepository.findById(anyLong())).willReturn(Optional.of(professional));
        given(salonServiceRepository.findById(anyLong())).willReturn(Optional.of(salonService));
        given(appointmentRepository.findPotentialConflicts(any(), any(), any())).willReturn(List.of(conflictingAppointment));

        assertThatThrownBy(() -> appointmentService.update(10L, updateRequestWithConflict))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Conflito de horário");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}