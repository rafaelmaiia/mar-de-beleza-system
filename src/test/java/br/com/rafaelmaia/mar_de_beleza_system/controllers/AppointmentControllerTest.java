package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.AppointmentItemRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
        "jwt.secret=c2V1LXNlZ3JlZG8tZm9ydGUtZS1sb25nby1wYXJhLXRlc3Rlcy1jb20tcGVsb21lbm9zLTY0LWJ5dGVzLWVtLWJhc2U2NA=="
}) // String válida para o contexto do teste
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void shouldCreateAppointmentSuccessfullyAndReturnStatus201() throws Exception {
        AppointmentItemRequestDTO item = new AppointmentItemRequestDTO(1L, 1L, new BigDecimal("50.00"));
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO(
                1L,
                LocalDateTime.now().plusDays(5), // Agendamento para daqui a 5 dias
                "Teste de integração",
                null,
                List.of(item)
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.client.name").value("Fernanda Lima"));
    }


}