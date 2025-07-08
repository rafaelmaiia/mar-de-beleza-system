package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.AppointmentStatus;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentItemRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "jwt.secret=c2V1LXNlZ3JlZG8tZm9ydGUtZS1sb25nby1wYXJhLXRlc3Rlcy1jb20tcGVsb21lbm9zLTY0LWJ5dGVzLWVtLWJhc2U2NA=="
}) // String válida para o contexto do teste
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long baseAppointmentId;

    @Test
    @Transactional
    @WithMockUser
    void shouldCreateAppointmentSuccessfullyAndReturnStatus201() throws Exception {
        AppointmentItemRequestDTO item = new AppointmentItemRequestDTO(1L, 1L, new BigDecimal("50.00"));
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO(
                1L,
                LocalDateTime.now().plusDays(5),
                "Teste de integração",
                null,
                List.of(item)
        );

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.client.name").value("Fernanda Lima"));
    }

    @Test
    @WithMockUser
    void shouldReturnFilteredAndPagedAppointmentsAndReturnStatus200() throws Exception {
        mockMvc.perform(get("/api/v1/appointments")
                    .param("date", "2025-06-15")
                    .param("page", "0")
                    .param("size", "5")
                    .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].client.name", is("Fernanda Lima")))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @Transactional
    @WithMockUser
    void shouldUpdateAppointmentSuccessfullyAndReturnStatus200() throws Exception {
        AppointmentItemRequestDTO item = new AppointmentItemRequestDTO(2L, 2L, new BigDecimal("220.00"));
        AppointmentRequestDTO updateRequestDTO = new AppointmentRequestDTO(
                2L,
                LocalDateTime.of(2025, 7, 10, 11, 0),
                "Observação foi atualizada.",
                AppointmentStatus.CONFIRMED,
                List.of(item)
        );

        mockMvc.perform(put("/api/v1/appointments/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.observations", is("Observação foi atualizada.")));
    }

    @Test
    @Order(1)
    @WithMockUser(authorities = "ROLE_ADMIN")
    void shouldCreateBaseAppointmentForConflictTest() throws Exception {
        LocalDateTime futureDate = LocalDate.now().plusDays(3).atTime(14, 0);
        AppointmentRequestDTO baseRequest = new AppointmentRequestDTO(
                1L, futureDate, "Agendamento base", null,
                List.of(new AppointmentItemRequestDTO(1L, 1L, new BigDecimal("50.00")))
        );

        String responseAsString = mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        AppointmentResponseDTO responseDTO = objectMapper.readValue(responseAsString, AppointmentResponseDTO.class);
        baseAppointmentId = responseDTO.id();
        assertThat(baseAppointmentId).isNotNull();
    }

    @Test
    @Order(2)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @WithMockUser
    void shouldReturnStatus409WhenUpdateCausesConflict() throws Exception {
        assertThat(baseAppointmentId).isNotNull();

        String baseAppointmentJson = mockMvc.perform(get("/api/v1/appointments/" + baseAppointmentId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        AppointmentResponseDTO baseAppointment = objectMapper.readValue(baseAppointmentJson, AppointmentResponseDTO.class);
        LocalDateTime conflictTime = baseAppointment.appointmentDate();

        AppointmentRequestDTO conflictUpdateRequestDTO = new AppointmentRequestDTO(
                3L, conflictTime, "Tentando criar um conflito", null,
                List.of(new AppointmentItemRequestDTO(1L, 1L, new BigDecimal("150.00")))
        );

        mockMvc.perform(put("/api/v1/appointments/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictUpdateRequestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    @WithMockUser
    void shouldDeleteAppointmentSuccessfullyAndReturnStatus204() throws Exception {
        mockMvc.perform(delete("/api/v1/appointments/3"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/appointments/3"))
                .andExpect(status().isNotFound());
    }
}