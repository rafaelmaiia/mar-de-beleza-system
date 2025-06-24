package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.*;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.dto.*;
import br.com.rafaelmaia.mar_de_beleza_system.repository.AppointmentRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ClientRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ProfessionalRepository;

import br.com.rafaelmaia.mar_de_beleza_system.repository.SalonServiceRepository;
import br.com.rafaelmaia.mar_de_beleza_system.services.AppointmentService;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public AppointmentDTO findAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Appointment not found with id " + id));
        return mapToDTO(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> findAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentDTO create(AppointmentRequestDTO request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ObjectNotFoundException("Client not found with id " + request.getClientId()));

        Appointment appointment = Appointment.builder()
                .client(client)
                .appointmentDate(request.getAppointmentDate())
                .observations(request.getObservations())
                .status(request.getStatus() != null ? request.getStatus() : AppointmentStatus.SCHEDULED)
                .services(new ArrayList<>()) // Inicializa a lista de services do salão
                .build();

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (AppointmentItemRequestDTO itemRequest : request.getItems()) {
                Professional professional = professionalRepository.findById(itemRequest.getProfessionalId())
                        .orElseThrow(() -> new ObjectNotFoundException("Professional not found with id " + itemRequest.getProfessionalId()));
                SalonService salonService = salonServiceRepository.findById(itemRequest.getSalonServiceId())
                        .orElseThrow(() -> new ObjectNotFoundException("SalonService not found with id " + itemRequest.getSalonServiceId()));

                AppointmentItem appointmentItem = AppointmentItem.builder()
                        .appointment(appointment)
                        .service(salonService)
                        .professional(professional)
                        .price(itemRequest.getPrice())
                        .build();
                appointment.getServices().add(appointmentItem);
            }
        }
        appointment = appointmentRepository.save(appointment);
        return mapToDTO(appointment);
    }

    @Override
    @Transactional
    public AppointmentDTO update(Long id, AppointmentRequestDTO request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Appointment not found with id " + id));

        if (request.getClientId() != null) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new ObjectNotFoundException("Client not found with id " + request.getClientId()));
            appointment.setClient(client);
        }
        if (request.getAppointmentDate() != null) {
            appointment.setAppointmentDate(request.getAppointmentDate());
        }
        if (request.getObservations() != null) {
            appointment.setObservations(request.getObservations());
        }
        if (request.getStatus() != null) {
            appointment.setStatus(request.getStatus());
        }

        appointment.getServices().clear(); // Remove os itens antigos da coleção gerenciada pelo Hibernate

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (AppointmentItemRequestDTO itemRequest : request.getItems()) {
                Professional professional = professionalRepository.findById(itemRequest.getProfessionalId())
                        .orElseThrow(() -> new ObjectNotFoundException("Professional not found with id " + itemRequest.getProfessionalId()));
                SalonService salonService = salonServiceRepository.findById(itemRequest.getSalonServiceId())
                        .orElseThrow(() -> new ObjectNotFoundException("SalonService not found with id " + itemRequest.getSalonServiceId()));

                AppointmentItem appointmentItem = AppointmentItem.builder()
                        .appointment(appointment)
                        .service(salonService)
                        .professional(professional)
                        .price(itemRequest.getPrice())
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

    private AppointmentDTO mapToDTO(Appointment appointment) {
        return AppointmentDTO.fromEntity(appointment);
    }
}
