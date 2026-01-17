package nl.gerimedica.assignment;

import nl.gerimedica.assignment.repository.AppointmentRepository;
import nl.gerimedica.assignment.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();
    }

    @Test
    void testValidRequest_ShouldSucceed() throws Exception {
        String validPayload = """
                {
                    "reasons": ["Checkup"],
                    "dates": ["2025-02-01"]
                }
                """;

        mockMvc.perform(post("/api/bulk-appointments")
                .param("patientName", "John Doe")
                .param("ssn", "123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPayload))
                .andExpect(status().isOk());
    }

    @Test
    void testBlankPatientName_ShouldReturnBadRequest() throws Exception {
        String validPayload = """
                {
                    "reasons": ["Checkup"],
                    "dates": ["2025-02-01"]
                }
                """;

        mockMvc.perform(post("/api/bulk-appointments")
                .param("patientName", "")
                .param("ssn", "123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testBlankSSN_ShouldReturnBadRequest() throws Exception {
        String validPayload = """
                {
                    "reasons": ["Checkup"],
                    "dates": ["2025-02-01"]
                }
                """;

        mockMvc.perform(post("/api/bulk-appointments")
                .param("patientName", "John Doe")
                .param("ssn", "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testInvalidSSNFormat_ShouldReturnBadRequest() throws Exception {
        String validPayload = """
                {
                    "reasons": ["Checkup"],
                    "dates": ["2025-02-01"]
                }
                """;

        mockMvc.perform(post("/api/bulk-appointments")
                .param("patientName", "John Doe")
                .param("ssn", "12345") // Invalid: not 9 digits
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testBlankKeyword_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/appointments-by-reason")
                .param("keyword", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testValidKeyword_ShouldSucceed() throws Exception {
        mockMvc.perform(get("/api/appointments-by-reason")
                .param("keyword", "Checkup"))
                .andExpect(status().isOk());
    }

    @Test
    void testBlankSSNForDelete_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/delete-appointments")
                .param("ssn", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testBlankSSNForLatest_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/appointments/latest")
                .param("ssn", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testErrorResponseStructure_ShouldContainAllFields() throws Exception {
        mockMvc.perform(get("/api/appointments-by-reason")
                .param("keyword", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").isMap());
    }
}
