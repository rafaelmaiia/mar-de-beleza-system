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
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public AppointmentResponseDTO findAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Appointment not found with id " + id));
        return mapToDTO(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> findAllAppointments(LocalDate date, Long professionalId, Long clientId) {
        // Cria uma especificação combinando os filtros
        Specification<Appointment> spec = AppointmentSpecification.withFilters(date, professionalId, clientId);

        // Usa o novo método findAll(spec) que veio do JpaSpecificationExecutor
        return appointmentRepository.findAll(spec).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentResponseDTO create(AppointmentRequestDTO request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ObjectNotFoundException("Client not found with id " + request.clientId()));

        Appointment appointment = Appointment.builder()
                .client(client)
                .appointmentDate(request.appointmentDate())
                .observations(request.observations())
                .status(request.status() != null ? request.status() : AppointmentStatus.SCHEDULED)
                .services(new ArrayList<>()) // Inicializa a lista de services do salão
                .build();

        if (request.items() != null && !request.items().isEmpty()) {
            for (AppointmentItemRequestDTO itemRequest : request.items()) {
                Professional professional = professionalRepository.findById(itemRequest.professionalId())
                        .orElseThrow(() -> new ObjectNotFoundException("Professional not found with id " + itemRequest.professionalId()));
                SalonService salonService = salonServiceRepository.findById(itemRequest.salonServiceId())
                        .orElseThrow(() -> new ObjectNotFoundException("SalonService not found with id " + itemRequest.salonServiceId()));

                AppointmentItem appointmentItem = AppointmentItem.builder()
                        .appointment(appointment)
                        .service(salonService)
                        .professional(professional)
                        .price(itemRequest.price())
                        .build();
                appointment.getServices().add(appointmentItem);
            }
        }
        appointment = appointmentRepository.save(appointment);
        return mapToDTO(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO update(Long id, AppointmentRequestDTO request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Appointment not found with id " + id));

        if (request.clientId() != null) {
            Client client = clientRepository.findById(request.clientId())
                    .orElseThrow(() -> new ObjectNotFoundException("Client not found with id " + request.clientId()));
            appointment.setClient(client);
        }
        if (request.appointmentDate() != null) {
            appointment.setAppointmentDate(request.appointmentDate());
        }
        if (request.observations() != null) {
            appointment.setObservations(request.observations());
        }
        if (request.status() != null) {
            appointment.setStatus(request.status());
        }

        appointment.getServices().clear(); // Remove os itens antigos da coleção gerenciada pelo Hibernate

        if (request.items() != null && !request.items().isEmpty()) {
            for (AppointmentItemRequestDTO itemRequest : request.items()) {
                Professional professional = professionalRepository.findById(itemRequest.professionalId())
                        .orElseThrow(() -> new ObjectNotFoundException("Professional not found with id " + itemRequest.professionalId()));
                SalonService salonService = salonServiceRepository.findById(itemRequest.salonServiceId())
                        .orElseThrow(() -> new ObjectNotFoundException("SalonService not found with id " + itemRequest.salonServiceId()));

                AppointmentItem appointmentItem = AppointmentItem.builder()
                        .appointment(appointment)
                        .service(salonService)
                        .professional(professional)
                        .price(itemRequest.price())
                        .build();
                appointment.getServices().add(appointmentItem);
            }
        }
        appointment = appointmentRepository.save(appointment);
        return mapToDTO(appointment);
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
