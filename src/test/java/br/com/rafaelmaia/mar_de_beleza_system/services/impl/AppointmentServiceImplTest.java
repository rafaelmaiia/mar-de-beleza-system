package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.*;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentItemRequestDTO;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

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
    private AppointmentItemRequestDTO itemDTO;

    @BeforeEach
    void setup() {
        // Padrão Arrange-Act-Assert / Given-When-Then
        // Arrange - criado os dados necessários para teste
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

        itemDTO = new AppointmentItemRequestDTO(salonService.getId(), professional.getId(), new BigDecimal("100.00"));

        requestDTO = new AppointmentRequestDTO(
            client.getId(),
            LocalDateTime.of(2025, 7, 8, 10, 0),
            "Observação de teste",
            AppointmentStatus.SCHEDULED,
            List.of(itemDTO)
        );
    }

    @Test
    void givenNoConflicts_whenCreateAppointment_thenAppointmentShouldBeCreatedSuccessfully() {
        // Given
        given(appointmentRepository.findPotentialConflicts(any(), any(), any())).willReturn(Collections.emptyList());

        given(clientRepository.findById(client.getId())).willReturn(Optional.of(client));
        given(professionalRepository.findById(professional.getId())).willReturn(Optional.of(professional));
        given(salonServiceRepository.findAllById(any())).willReturn(List.of(salonService));

        given(appointmentRepository.save(any(Appointment.class))).willAnswer(invocation -> {
           Appointment appointmentToSave = invocation.getArgument(0);
           appointmentToSave.setId(10L);
           return appointmentToSave;
        });

        // When
        AppointmentResponseDTO createdAppointment = appointmentService.create(requestDTO);

        // Then
        assertThat(createdAppointment).isNotNull();
        assertThat(createdAppointment.id()).isEqualTo(10L);
        assertThat(createdAppointment.client().name()).isEqualTo("Cliente Teste");
        assertThat(createdAppointment.status()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void givenExistingConflict_whenCreateAppointment_thenShouldThrowBusinessRuleException() {
        // Given
        // Criando um agendamento FALSO que já existe no banco
        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentDate(LocalDateTime.of(2025, 7, 8, 9, 30)); // Começa 30 min antes
        SalonService existingService = new SalonService();
        existingService.setDurationInMinutes(60); // Termina às 10:30, conflitando com o novo agendamento das 10:00
        AppointmentItem existingItem = new AppointmentItem();
        existingItem.setService(existingService);
        existingAppointment.setServices(List.of(existingItem));

        given(salonServiceRepository.findAllById(any())).willReturn(List.of(salonService));

        given(appointmentRepository.findPotentialConflicts(any(), any(), any())).willReturn(List.of(existingAppointment));

        // When & Then
        // Ao tentar executar o appointmentService.create uma BusinessRuleException é lançada
        assertThatThrownBy(() -> appointmentService.create(requestDTO))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("Conflito de horário");

        // Verificando que o metodo save NUNCA foi chamado, e provando que o fluxo foi interrompido
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void givenValidChanges_whenUpdateAppointment_thenShouldReturnUpdatedAppointment() {
        // Given

        // O 'willAnswer' retornará uma cópia do objeto para simular como o JPA funciona
        given(appointmentRepository.findById(anyLong())).willAnswer(invocation -> {
            Appointment originalAppointment = new Appointment();
            originalAppointment.setId(10L); // ID fixo para o teste
            originalAppointment.setAppointmentDate(requestDTO.appointmentDate());
            originalAppointment.setClient(client);

            List<AppointmentItem> items = new ArrayList<>();
            items.add(new AppointmentItem(1L, null, salonService, professional, new BigDecimal("100.00")));
            originalAppointment.setServices(items);

            return Optional.of(originalAppointment);
        });

        // O mock vai encontrar o serviço do requestDTO
        given(salonServiceRepository.findAllById(any())).willReturn(List.of(salonService));

        // O repositório de CLIENT vai encontrar o cliente
        given(clientRepository.findById(client.getId())).willReturn(Optional.of(client));

        // O repositório de PROFESSIONAL vai encontrar o professional
        given(professionalRepository.findById(professional.getId())).willReturn(Optional.of(professional));

        // O repositório não vai encontrar NENHUM conflito de horário
        given(appointmentRepository.findPotentialConflicts(any(), any(), any())).willReturn(Collections.emptyList());

        // O repositório salva e retorna o agendamento modificado
        given(appointmentRepository.save(any(Appointment.class))).willAnswer(invocation -> invocation.getArgument(0));

        // Preparando DTO de requisição com os novos dados
        LocalDateTime newDate = LocalDateTime.of(2025, 8, 15, 14, 0);
        AppointmentRequestDTO updateRequest = new AppointmentRequestDTO(
                client.getId(),
                newDate,
                "Observação ATUALIZADA",
                AppointmentStatus.CONFIRMED,
                List.of(itemDTO)
        );

        // When
        // Executando o metodo de atualização
        AppointmentResponseDTO updatedAppointment = appointmentService.update(10L, updateRequest);

        // Then
        // Verificando se o resultado está correto
        assertThat(updatedAppointment).isNotNull();
        assertThat(updatedAppointment.id()).isEqualTo(10L);
        assertThat(updatedAppointment.appointmentDate()).isEqualTo(newDate);
        assertThat(updatedAppointment.observations()).isEqualTo("Observação ATUALIZADA");
        assertThat(updatedAppointment.status()).isEqualTo(AppointmentStatus.CONFIRMED);
    }

    @Test
    void givenConflictWithAnotherAppointment_whenUpdateAppointment_thenShouldThrowBusinessRuleException() {
        // Given

        // O agendamento que queremos ATUALIZAR (ID 10)
        Appointment appointmentToUpdate = new Appointment();
        appointmentToUpdate.setId(10L);
        appointmentToUpdate.setAppointmentDate(LocalDateTime.of(2025, 7, 8, 10, 0));

        // O agendamento que JÁ EXISTE e vai causar o conflito (ID 11)
        Appointment conflictingAppointment = new Appointment();
        conflictingAppointment.setId(11L);
        conflictingAppointment.setAppointmentDate(LocalDateTime.of(2025, 7, 8, 14, 0));
        SalonService existingService = new SalonService();
        existingService.setDurationInMinutes(60); // Vai das 14:00 às 15:00
        conflictingAppointment.setServices(List.of(new AppointmentItem(2L, null, existingService, professional, new BigDecimal("100.00"))));

        // Quando o serviço procurar pelo agendamento a ser atualizado (ID 10), ele o encontrará
        given(appointmentRepository.findById(10L)).willReturn(Optional.of(appointmentToUpdate));

        // Quando o serviço procurar por conflitos, ele encontrará o agendamento de ID 11
        given(appointmentRepository.findPotentialConflicts(any(), any(), any())).willReturn(List.of(conflictingAppointment));

        // O mock do serviço retorna os dados do DTO
        given(salonServiceRepository.findAllById(any())).willReturn(List.of(salonService));

        // A requisição de update tenta mover o agendamento ID 10 para as 14:30 (o que conflita com o de ID 11)
        AppointmentRequestDTO updateRequestWithConflict = new AppointmentRequestDTO(
                client.getId(),
                LocalDateTime.of(2025, 7, 8, 14, 30),
                "Tentando mover para horário ocupado",
                AppointmentStatus.SCHEDULED,
                List.of(itemDTO) // O serviço de teste dura 60 min, então iria até 15:30
        );

        // When & Then
        // Verificando se a exceção correta é lançada
        assertThatThrownBy(() -> appointmentService.update(10L, updateRequestWithConflict))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Conflito de horário");

        // E garantindo que o metodo 'save' nunca foi chamado
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}