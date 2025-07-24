package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Appointment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Client;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SalonService;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SystemUser;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.StatusUpdateRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.AppointmentRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ClientRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.SalonServiceRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.SystemUserRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.specification.AppointmentSpecification;
import br.com.rafaelmaia.mar_de_beleza_system.services.AppointmentService;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;

    private final ClientRepository clientRepository;

    private final SystemUserRepository systemUserRepository;

    private final SalonServiceRepository salonServiceRepository;

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDTO findAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Appointment not found with id " + id));
        return mapToDTO(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> findAllAppointments(LocalDate date, Long professionalId, Long clientId, Pageable pageable) {
        // Aplica filtros dinâmicos por data, cliente e profissional
        Specification<Appointment> spec = AppointmentSpecification.withFilters(date, professionalId, clientId);

        Page<Appointment> appointmentPage = appointmentRepository.findAll(spec, pageable);

        return appointmentPage.map(AppointmentResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO create(AppointmentRequestDTO request) {
        logger.info("Iniciando processo de criação de agendamento para o cliente ID: {}", request.clientId());

        // 1. Busca as entidades principais que serão associadas ao agendamento
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ObjectNotFoundException("Cliente não encontrado com id " + request.clientId()));

        SystemUser professional = systemUserRepository.findById(request.professionalId())
                .orElseThrow(() -> new ObjectNotFoundException("Profissional não encontrado com id " + request.professionalId()));

        SalonService service = salonServiceRepository.findById(request.salonServiceId())
                .orElseThrow(() -> new ObjectNotFoundException("Serviço não encontrado com id " + request.salonServiceId()));

        // --- LÓGICA DE VALIDAÇÃO DE CONFLITO ---
        LocalDateTime startTime = request.appointmentDate();
        LocalDateTime endTime = startTime.plusMinutes(service.getDurationInMinutes());

        logger.debug("Verificando disponibilidade para o profissional ID {} no horário: {} a {}", professional.getId(), startTime.toLocalTime(), endTime.toLocalTime());

        LocalDateTime startOfDay = startTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startTime.toLocalDate().plusDays(1).atStartOfDay();
        List<Appointment> existingAppointments = appointmentRepository.findPotentialConflicts(professional.getId(), startOfDay, endOfDay);

        for (Appointment existing : existingAppointments) {
            // Calcula o fim do agendamento existente
            LocalDateTime existingStartTime = existing.getAppointmentDate();
            LocalDateTime existingEndTime = existingStartTime.plusMinutes(existing.getService().getDurationInMinutes());

            // Lógica de sobreposição: (Início A < Fim B) E (Fim A > Início B)
            if (startTime.isBefore(existingEndTime) && endTime.isAfter(existingStartTime)) {
                logger.warn("Conflito de horário detectado. Novo agendamento para {} colide com agendamento existente ID {}", professional.getName(), existing.getId());
                throw new BusinessRuleException(
                        "Conflito de horário: O profissional já tem um agendamento das " +
                                existingStartTime.toLocalTime() + " às " + existingEndTime.toLocalTime()
                );
            }
        }

        logger.info("Nenhum conflito de horário encontrado. Prosseguindo com a criação.");

        // --- CRIAÇÃO DA NOVA ENTIDADE ---
        Appointment newAppointment = Appointment.builder()
                .client(client)
                .professional(professional)
                .service(service)
                .appointmentDate(startTime)
                .price(request.price())
                .observations(request.observations())
                .status(request.status() != null ? request.status() : AppointmentStatus.SCHEDULED)
                .build();

        Appointment savedAppointment = appointmentRepository.save(newAppointment);
        logger.info("Agendamento ID {} criado com sucesso.", savedAppointment.getId());

        return AppointmentResponseDTO.fromEntity(savedAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO update(Long id, AppointmentRequestDTO request) {
        logger.info("Iniciando processo de atualização para o agendamento ID: {}", id);

        // 1. Busca o agendamento que queremos atualizar
        Appointment appointmentToUpdate = appointmentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Agendamento não encontrado com ID: " + id));

        // 2. Busca as entidades que podem ter sido alteradas na requisição
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ObjectNotFoundException("Cliente não encontrado com id " + request.clientId()));
        SystemUser professional = systemUserRepository.findById(request.professionalId())
                .orElseThrow(() -> new ObjectNotFoundException("Profissional não encontrado com id " + request.professionalId()));
        SalonService service = salonServiceRepository.findById(request.salonServiceId())
                .orElseThrow(() -> new ObjectNotFoundException("Serviço não encontrado com id " + request.salonServiceId()));

        // --- LÓGICA DE VALIDAÇÃO DE CONFLITO ---
        LocalDateTime startTime = request.appointmentDate();
        LocalDateTime endTime = startTime.plusMinutes(service.getDurationInMinutes());

        logger.debug("Verificando disponibilidade para o profissional ID {} no horário: {} a {}", professional.getId(), startTime.toLocalTime(), endTime.toLocalTime());

        LocalDateTime startOfDay = startTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startTime.toLocalDate().plusDays(1).atStartOfDay();
        List<Appointment> existingAppointments = appointmentRepository.findPotentialConflicts(professional.getId(), startOfDay, endOfDay);

        for (Appointment existing : existingAppointments) {
            // A lógica CRUCIAL do update: ignora o próprio agendamento na checagem
            if (existing.getId().equals(id)) {
                continue;
            }

            LocalDateTime existingStartTime = existing.getAppointmentDate();
            LocalDateTime existingEndTime = existingStartTime.plusMinutes(existing.getService().getDurationInMinutes());

            if (startTime.isBefore(existingEndTime) && endTime.isAfter(existingStartTime)) {
                logger.warn("Conflito de horário detectado na atualização. Agendamento ID {} colide com agendamento existente ID {}", id, existing.getId());
                throw new BusinessRuleException(
                        "Conflito de horário: O profissional já tem um agendamento das " +
                                existingStartTime.toLocalTime() + " às " + existingEndTime.toLocalTime()
                );
            }
        }

        logger.info("Nenhum conflito de horário encontrado para a atualização.");

        // --- ATUALIZAÇÃO DA ENTIDADE ---
        appointmentToUpdate.setClient(client);
        appointmentToUpdate.setProfessional(professional);
        appointmentToUpdate.setService(service);
        appointmentToUpdate.setAppointmentDate(startTime);
        appointmentToUpdate.setPrice(request.price());
        appointmentToUpdate.setObservations(request.observations());
        // appointmentToUpdate.setStatus(request.status()); Há um metodo próprio para edição de status

        Appointment updatedAppointment = appointmentRepository.save(appointmentToUpdate);
        logger.info("Agendamento ID {} atualizado com sucesso.", updatedAppointment.getId());

        return AppointmentResponseDTO.fromEntity(updatedAppointment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ObjectNotFoundException("Appointment not found with id " + id);
        }
        appointmentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO updateStatus(Long id, StatusUpdateRequestDTO statusUpdateDTO) {
        logger.info("Atualizando status do agendamento ID: {} para {}", id, statusUpdateDTO.status());
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Agendamento não encontrado com ID: " + id));

        appointment.setStatus(statusUpdateDTO.status());
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return AppointmentResponseDTO.fromEntity(updatedAppointment);
    }

    private AppointmentResponseDTO mapToDTO(Appointment appointment) {
        return AppointmentResponseDTO.fromEntity(appointment);
    }
}
