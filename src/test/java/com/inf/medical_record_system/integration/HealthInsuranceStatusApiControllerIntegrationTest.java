package com.inf.medical_record_system.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inf.medical_record_system.dto.HealthInsuranceStatusDTO;
import com.inf.medical_record_system.dto.HealthInsuranceStatusRequestDTO;
import com.inf.medical_record_system.service.HealthInsuranceStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HealthInsuranceStatusApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HealthInsuranceStatusService healthInsuranceStatusService;

    private HealthInsuranceStatusDTO statusDTO;
    private HealthInsuranceStatusRequestDTO requestDTO;

    @BeforeEach
    void init() {
        statusDTO = new HealthInsuranceStatusDTO();
        statusDTO.setId(1L);
        statusDTO.setPatientId(1L);
        statusDTO.setPatientName("Mario Slavov");
        statusDTO.setYear(2026);
        statusDTO.setMonth(5);
        statusDTO.setInsured(true);

        requestDTO = new HealthInsuranceStatusRequestDTO();
        requestDTO.setPatientId(1L);
        requestDTO.setYear(2026);
        requestDTO.setMonth(5);
        requestDTO.setInsured(true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllStatusesShouldReturnOkForAuthenticatedAdmin() throws Exception {
        given(healthInsuranceStatusService.getAllStatuses())
                .willReturn(List.of(statusDTO));

        mockMvc.perform(get("/api/health-insurance-statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].patientId").value(1))
                .andExpect(jsonPath("$[0].patientName").value("Mario Slavov"))
                .andExpect(jsonPath("$[0].year").value(2026))
                .andExpect(jsonPath("$[0].month").value(5))
                .andExpect(jsonPath("$[0].insured").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStatusShouldReturnCreatedForAuthenticatedAdminWithValidCsrf() throws Exception {
        given(healthInsuranceStatusService.createStatus(any(HealthInsuranceStatusRequestDTO.class)))
                .willReturn(statusDTO);

        mockMvc.perform(post("/api/health-insurance-statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.patientName").value("Mario Slavov"))
                .andExpect(jsonPath("$.year").value(2026))
                .andExpect(jsonPath("$.month").value(5))
                .andExpect(jsonPath("$.insured").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStatusShouldReturnCreatedWithoutCsrfBecauseApiCsrfIsDisabled() throws Exception {
        given(healthInsuranceStatusService.createStatus(any(HealthInsuranceStatusRequestDTO.class)))
                .willReturn(statusDTO);

        mockMvc.perform(post("/api/health-insurance-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.insured").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStatusShouldReturnBadRequestWhenMonthIsInvalid() throws Exception {
        requestDTO.setMonth(13);

        mockMvc.perform(post("/api/health-insurance-statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void getAllStatusesShouldReturnUnauthorizedForAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/health-insurance-statuses"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @WithAnonymousUser
    void createStatusShouldReturnUnauthorizedForAnonymousUser() throws Exception {
        mockMvc.perform(post("/api/health-insurance-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }
}