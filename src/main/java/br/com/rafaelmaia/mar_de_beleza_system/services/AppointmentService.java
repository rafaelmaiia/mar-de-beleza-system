package br.com.rafaelmaia.mar_de_beleza_system.services;


import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;

import java.util.List;

public interface AppointmentService {

    AppointmentDTO findAppointmentById(Long id);
    List<AppointmentDTO> findAllAppointments();
    AppointmentDTO create(AppointmentRequestDTO obj);
    AppointmentDTO update(Long id, AppointmentRequestDTO obj);
    void delete(Long id);
}
