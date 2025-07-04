package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    AppointmentResponseDTO findAppointmentById(Long id);
    List<AppointmentResponseDTO> findAllAppointments(LocalDate date, Long professionalId, Long clientId);
    AppointmentResponseDTO create(AppointmentRequestDTO obj);
    AppointmentResponseDTO update(Long id, AppointmentRequestDTO obj);
    void delete(Long id);
}
