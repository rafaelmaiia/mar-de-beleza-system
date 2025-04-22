package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Appointment;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Client;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Professional;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ClientDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.ProfessionalDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.AppointmentRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ClientRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ProfessionalRepository;

import br.com.rafaelmaia.mar_de_beleza_system.services.AppointmentService;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private final AppointmentRepository appointmentRepository;

    @Autowired
    private final ClientRepository clientRepository;

    @Autowired
    private final ProfessionalRepository professionalRepository;

    @Override
    public AppointmentDTO findAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Appointment not found with id " + id));
        return mapToDTO(appointment);
    }

    @Override
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
        Professional professional = professionalRepository.findById(request.getProfessionalId())
                .orElseThrow(() -> new ObjectNotFoundException("Professional not found with id " + request.getProfessionalId()));

        Appointment appointment = Appointment.builder()
                .client(client)
                .professional(professional)
                .appointmentDate(request.getAppointmentDate())
                .serviceType(request.getServiceName())
                .observations(request.getObservations())
                .status(request.getStatus() != null ? request.getStatus() : AppointmentStatus.SCHEDULED)
                .build();
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
        if (request.getProfessionalId() != null) {
            Professional professional = professionalRepository.findById(request.getProfessionalId())
                    .orElseThrow(() -> new ObjectNotFoundException("Professional not found with id " + request.getProfessionalId()));
            appointment.setProfessional(professional);
        }
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setServiceType(request.getServiceName());
        appointment.setObservations(request.getObservations());
        appointment.setStatus(request.getStatus());

        appointment = appointmentRepository.save(appointment);
        return mapToDTO(appointment);
    }

    @Override
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ObjectNotFoundException("Appointment not found with id " + id);
        }
        appointmentRepository.deleteById(id);
    }

    private AppointmentDTO mapToDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .client(ClientDTO.fromEntity(appointment.getClient()))
                .professional(ProfessionalDTO.fromEntity(appointment.getProfessional()))
                .appointmentDate(appointment.getAppointmentDate())
                .serviceName(appointment.getServiceType())
                .status(appointment.getStatus())
                .observations(appointment.getObservations())
                .build();
    }
}
