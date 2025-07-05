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
import br.com.rafaelmaia.mar_de_beleza_system.repository.specification.AppointmentSpecification;
import br.com.rafaelmaia.mar_de_beleza_system.services.AppointmentService;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.BusinessRuleException;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final ClientRepository clientRepository;

    private final ProfessionalRepository professionalRepository;

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
        // Validação de conflitos com agendamentos existentes

        // Carrega os serviços para obter duração total e validar existência
        List<Long> serviceIds = request.items().stream()
                .map(AppointmentItemRequestDTO::salonServiceId).toList();
        List<SalonService> requestedServices = salonServiceRepository.findAllById(serviceIds);

        // Garante que todos os serviços solicitados foram encontrados
        if(requestedServices.size() != serviceIds.size()){
            throw new ObjectNotFoundException("Um ou mais serviços não foram encontrados.");
        }

        // Calcula início e término do novo agendamento
        int totalDurationInMinutes = requestedServices.stream().mapToInt(SalonService::getDurationInMinutes).sum();
        LocalDateTime newAppointmentStartTime = request.appointmentDate();
        LocalDateTime newAppointmentEndTime = newAppointmentStartTime.plusMinutes(totalDurationInMinutes);

        // Verifica se algum profissional já está ocupado nesse horário
        for (AppointmentItemRequestDTO item : request.items()) {
            Long professionalId = item.professionalId();

            LocalDateTime startOfDay = newAppointmentStartTime.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = newAppointmentStartTime.toLocalDate().plusDays(1).atStartOfDay();
            List<Appointment> existingAppointments = appointmentRepository.findPotentialConflicts(professionalId, startOfDay, endOfDay);

            for (Appointment existingAppointment : existingAppointments) {
                int existingDuration = existingAppointment.getServices().stream()
                        .mapToInt(serviceItem -> serviceItem.getService().getDurationInMinutes()).sum();
                LocalDateTime existingStartTime = existingAppointment.getAppointmentDate();
                LocalDateTime existingEndTime = existingStartTime.plusMinutes(existingDuration);

                if (newAppointmentStartTime.isBefore(existingEndTime) && newAppointmentEndTime.isAfter(existingStartTime)) {
                    throw new BusinessRuleException(
                            "Conflito de horário: O profissional já tem um agendamento das " +
                                    existingStartTime.toLocalTime() + " às " + existingEndTime.toLocalTime()
                    );
                }
            }
        }

        // Criação do agendamento após passar nas validações

        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ObjectNotFoundException("Cliente não encontrado com id " + request.clientId()));

        // Usando o Builder da Entidade
        Appointment appointment = Appointment.builder()
                .client(client)
                .appointmentDate(request.appointmentDate())
                .observations(request.observations())
                .status(request.status() != null ? request.status() : AppointmentStatus.SCHEDULED)
                .services(new ArrayList<>())
                .build();

        final Appointment finalAppointment = appointment;

        // Mapeia os itens do agendamento (profissional + serviço + preço)
        List<AppointmentItem> appointmentItems = request.items().stream().map(itemRequest -> {
            Professional professional = professionalRepository.findById(itemRequest.professionalId())
                    .orElseThrow(() -> new ObjectNotFoundException("Profissional não encontrado com id " + itemRequest.professionalId()));

            // Reutiliza a lista carregada para evitar nova consulta ao banco
            SalonService salonService = requestedServices.stream()
                    .filter(s -> s.getId().equals(itemRequest.salonServiceId()))
                    .findFirst()
                    .orElseThrow(() -> new ObjectNotFoundException("Erro interno: Serviço não encontrado na lista pré-carregada."));

            return AppointmentItem.builder()
                    .appointment(finalAppointment)
                    .service(salonService)
                    .professional(professional)
                    .price(itemRequest.price())
                    .build();
        }).toList();

        appointment.setServices(appointmentItems);

        appointment = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.fromEntity(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO update(Long id, AppointmentRequestDTO request) {
        // Busca o agendamento que já existe no banco
        Appointment appointmentToUpdate = appointmentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Agendamento não encontrado com ID: " + id));

        // Validação de conflitos para atualização
        // Calcula os dados do horário solicitado na atualização
        List<Long> serviceIds = request.items().stream().map(AppointmentItemRequestDTO::salonServiceId).toList();
        List<SalonService> requestedServices = salonServiceRepository.findAllById(serviceIds);

        if (requestedServices.size() != serviceIds.size()) {
            throw new ObjectNotFoundException("Um ou mais serviços com os IDs fornecidos não foram encontrados.");
        }

        int totalDurationInMinutes = requestedServices.stream().mapToInt(SalonService::getDurationInMinutes).sum();
        LocalDateTime newAppointmentStartTime = request.appointmentDate();
        LocalDateTime newAppointmentEndTime = newAppointmentStartTime.plusMinutes(totalDurationInMinutes);

        // Verifica o conflito para cada profissional
        for (AppointmentItemRequestDTO item : request.items()) {
            Long professionalId = item.professionalId();
            LocalDateTime startOfDay = newAppointmentStartTime.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = newAppointmentStartTime.toLocalDate().plusDays(1).atStartOfDay();
            List<Appointment> existingAppointments = appointmentRepository.findPotentialConflicts(professionalId, startOfDay, endOfDay);

            for (Appointment existingAppointment : existingAppointments) {
                // Ignora o próprio agendamento para evitar falso positivo
                if (existingAppointment.getId().equals(id)) {
                    continue;
                }

                int existingDuration = existingAppointment.getServices().stream().mapToInt(i -> i.getService().getDurationInMinutes()).sum();
                LocalDateTime existingStartTime = existingAppointment.getAppointmentDate();
                LocalDateTime existingEndTime = existingStartTime.plusMinutes(existingDuration);

                if (newAppointmentStartTime.isBefore(existingEndTime) && newAppointmentEndTime.isAfter(existingStartTime)) {
                    throw new BusinessRuleException("Conflito de horário: O profissional já tem um agendamento das " +
                            existingStartTime.toLocalTime() + " às " + existingEndTime.toLocalTime());
                }
            }
        }

        // Atualiza os dados do agendamento após validações
        appointmentToUpdate.setAppointmentDate(request.appointmentDate());
        appointmentToUpdate.setObservations(request.observations());
        appointmentToUpdate.setStatus(request.status());

        // Permite alterar o cliente vinculado ao agendamento
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ObjectNotFoundException("Cliente não encontrado com id " + request.clientId()));
        appointmentToUpdate.setClient(client);

        // Limpa e Substitui - O Hibernate se encarrega de deletar os antigos e inserir os novos no banco
        appointmentToUpdate.getServices().clear();

        List<AppointmentItem> appointmentItems = request.items().stream().map(itemRequest -> {
            Professional professional = professionalRepository.findById(itemRequest.professionalId())
                    .orElseThrow(() -> new ObjectNotFoundException("Profissional não encontrado com id " + itemRequest.professionalId()));
            SalonService salonService = requestedServices.stream()
                    .filter(s -> s.getId().equals(itemRequest.salonServiceId()))
                    .findFirst()
                    .orElseThrow(() -> new ObjectNotFoundException("Erro interno: Serviço não encontrado na lista pré-carregada."));

            return AppointmentItem.builder()
                    .appointment(appointmentToUpdate)
                    .service(salonService)
                    .professional(professional)
                    .price(itemRequest.price())
                    .build();
        }).toList();

        appointmentToUpdate.getServices().addAll(appointmentItems);

        Appointment updatedAppointment = appointmentRepository.save(appointmentToUpdate);

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

    private AppointmentResponseDTO mapToDTO(Appointment appointment) {
        return AppointmentResponseDTO.fromEntity(appointment);
    }
}
