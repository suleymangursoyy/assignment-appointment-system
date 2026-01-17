package nl.gerimedica.assignment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nl.gerimedica.assignment.enums.Reason;
import nl.gerimedica.assignment.service.HospitalService;
import nl.gerimedica.assignment.service.dto.AppointmentDTO;
import nl.gerimedica.assignment.util.HospitalUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "Appointment Management", description = "APIs for managing patient appointments in the hospital system")
public class AppointmentController {

    private final HospitalService hospitalService;

    public AppointmentController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

  /**
   * Example: {
   * "reasons": ["Checkup", "Follow-up", "X-Ray"],
   * "dates": ["2025-02-01", "2025-02-15", "2025-03-01"]
   * }
   */
  @PostMapping("/bulk-appointments")
  public ResponseEntity<List<AppointmentDTO>> createBulkAppointments(
      @RequestParam @NotBlank(message = "Patient name is required") String patientName,
      @RequestParam @NotBlank(message = "SSN is required") String ssn,
      @RequestBody @Valid Map<String, List<String>> payload) {
    List<String> reasons = payload.get("reasons");
    List<String> dates = payload.get("dates");

        HospitalUtils.recordUsage("Controller triggered bulk appointments creation");

        List<AppointmentDTO> created = hospitalService.bulkCreateAppointments(patientName, ssn, reasons, dates);
        return new ResponseEntity<>(created, HttpStatus.OK);
    }

  @GetMapping("/appointments-by-reason")
  public ResponseEntity<List<AppointmentDTO>> getAppointmentsByReason(
      @RequestParam @NotNull(message = "Reason is required") Reason reason) {
    List<AppointmentDTO> found = hospitalService.getAppointmentsByReason(reason);
    return new ResponseEntity<>(found, HttpStatus.OK);
  }

  @PostMapping("/delete-appointments")
  public ResponseEntity<String> deleteAppointmentsBySSN(
      @RequestParam @NotBlank(message = "SSN is required") String ssn) {
    hospitalService.deleteAppointmentsBySSN(ssn);
    return new ResponseEntity<>("Deleted all appointments for SSN: " + ssn, HttpStatus.OK);
  }

  @GetMapping("/appointments/latest")
  public ResponseEntity<AppointmentDTO> getLatestAppointment(
      @RequestParam @NotBlank(message = "SSN is required") String ssn) {
    AppointmentDTO latest = hospitalService.findLatestAppointmentBySSN(ssn);
    return new ResponseEntity<>(latest, HttpStatus.OK);
  }
}
