package com.inf.medical_record_system.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inf.medical_record_system.data.entity.PaymentSource;
import com.inf.medical_record_system.dto.ExaminationDTO;
import com.inf.medical_record_system.dto.ExaminationRequestDTO;
import com.inf.medical_record_system.service.ExaminationService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExaminationApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExaminationService examinationService;

    private ExaminationDTO examinationDTO;
    private ExaminationRequestDTO requestDTO;

    @BeforeEach
    void init() {
        LocalDate validExaminationDate = LocalDate.now().minusDays(1);

        examinationDTO = new ExaminationDTO();
        examinationDTO.setId(1L);
        examinationDTO.setExaminationDate(validExaminationDate);
        examinationDTO.setDoctorId(1L);
        examinationDTO.setDoctorName("Dr. Ivan Petrov");
        examinationDTO.setPatientId(1L);
        examinationDTO.setPatientName("Mario Slavov");
        examinationDTO.setDiagnosisId(1L);
        examinationDTO.setDiagnosisName("Common cold");
        examinationDTO.setPrice(BigDecimal.valueOf(80.00));
        examinationDTO.setPaymentSource(PaymentSource.NHIF);

        requestDTO = new ExaminationRequestDTO();
        requestDTO.setExaminationDate(validExaminationDate);
        requestDTO.setDoctorId(1L);
        requestDTO.setPatientId(1L);
        requestDTO.setDiagnosisId(1L);
        requestDTO.setPrice(BigDecimal.valueOf(80.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllExaminationsShouldReturnOkForAuthenticatedAdmin() throws Exception {
        given(examinationService.getAllExaminations())
                .willReturn(List.of(examinationDTO));

        mockMvc.perform(get("/api/examinations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].doctorName").value("Dr. Ivan Petrov"))
                .andExpect(jsonPath("$[0].patientName").value("Mario Slavov"))
                .andExpect(jsonPath("$[0].paymentSource").value("NHIF"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getExaminationByIdShouldReturnOkForAuthenticatedAdmin() throws Exception {
        given(examinationService.getExaminationById(1L))
                .willReturn(examinationDTO);

        mockMvc.perform(get("/api/examinations/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorName").value("Dr. Ivan Petrov"))
                .andExpect(jsonPath("$.patientName").value("Mario Slavov"))
                .andExpect(jsonPath("$.paymentSource").value("NHIF"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createExaminationShouldReturnCreatedForAuthenticatedAdminWithValidCsrf() throws Exception {
        given(examinationService.createExamination(any(ExaminationRequestDTO.class)))
                .willReturn(examinationDTO);

        mockMvc.perform(post("/api/examinations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorName").value("Dr. Ivan Petrov"))
                .andExpect(jsonPath("$.patientName").value("Mario Slavov"))
                .andExpect(jsonPath("$.paymentSource").value("NHIF"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createExaminationShouldReturnCreatedWithoutCsrfBecauseApiCsrfIsDisabled() throws Exception {
        given(examinationService.createExamination(any(ExaminationRequestDTO.class)))
                .willReturn(examinationDTO);

        mockMvc.perform(post("/api/examinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorName").value("Dr. Ivan Petrov"))
                .andExpect(jsonPath("$.patientName").value("Mario Slavov"))
                .andExpect(jsonPath("$.paymentSource").value("NHIF"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createExaminationShouldReturnBadRequestWhenDateIsInFuture() throws Exception {
        requestDTO.setExaminationDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/examinations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteExaminationShouldReturnNoContentForAuthenticatedAdminWithValidCsrf() throws Exception {
        mockMvc.perform(delete("/api/examinations/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void getAllExaminationsShouldReturnUnauthorizedForAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/examinations"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateExaminationShouldReturnOkForAuthenticatedAdminWithValidCsrf() throws Exception {
        given(examinationService.updateExamination(eq(1L), any(ExaminationRequestDTO.class)))
                .willReturn(examinationDTO);

        mockMvc.perform(put("/api/examinations/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorName").value("Dr. Ivan Petrov"))
                .andExpect(jsonPath("$.patientName").value("Mario Slavov"));
    }
}