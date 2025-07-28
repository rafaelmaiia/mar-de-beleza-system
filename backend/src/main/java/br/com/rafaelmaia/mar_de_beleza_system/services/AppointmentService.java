package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.StatusUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AppointmentService {

    AppointmentResponseDTO findAppointmentById(Long id);
    Page<AppointmentResponseDTO> findAllAppointments(LocalDate startDate, LocalDate endDate, Long professionalId, Long clientId, String status, Pageable pageable);
    Page<AppointmentResponseDTO> findAppointmentsByDate(LocalDate date, Pageable pageable);
    AppointmentResponseDTO create(AppointmentRequestDTO obj);
    AppointmentResponseDTO update(Long id, AppointmentRequestDTO obj);
    void delete(Long id);
    AppointmentResponseDTO updateStatus(Long id, StatusUpdateRequestDTO statusUpdateDTO);
}
