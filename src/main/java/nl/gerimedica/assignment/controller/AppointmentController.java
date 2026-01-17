package nl.gerimedica.assignment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import nl.gerimedica.assignment.enums.Reason;
import nl.gerimedica.assignment.service.HospitalService;
import nl.gerimedica.assignment.util.HospitalUtils;
import nl.gerimedica.assignment.repository.entity.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
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
  public ResponseEntity<List<Appointment>> createBulkAppointments(
      @RequestParam @NotBlank(message = "Patient name is required") String patientName,
      @RequestParam @NotBlank(message = "SSN is required") String ssn,
      @RequestBody @Valid Map<String, List<String>> payload) {
    List<String> reasons = payload.get("reasons");
    List<String> dates = payload.get("dates");

    HospitalUtils.recordUsage("Controller triggered bulk appointments creation");

    List<Appointment> created = hospitalService.bulkCreateAppointments(patientName, ssn, reasons, dates);
    return new ResponseEntity<>(created, HttpStatus.OK);
  }

  @GetMapping("/appointments-by-reason")
  public ResponseEntity<List<Appointment>> getAppointmentsByReason(
      @RequestParam @NotBlank(message = "Keyword is required") Reason reason) {
    List<Appointment> found = hospitalService.getAppointmentsByReason(reason);
    return new ResponseEntity<>(found, HttpStatus.OK);
  }

  @PostMapping("/delete-appointments")
  public ResponseEntity<String> deleteAppointmentsBySSN(
      @RequestParam @NotBlank(message = "SSN is required") String ssn) {
    hospitalService.deleteAppointmentsBySSN(ssn);
    return new ResponseEntity<>("Deleted all appointments for SSN: " + ssn, HttpStatus.OK);
  }

  @GetMapping("/appointments/latest")
  public ResponseEntity<Appointment> getLatestAppointment(
      @RequestParam @NotBlank(message = "SSN is required") String ssn) {
    Appointment latest = hospitalService.findLatestAppointmentBySSN(ssn);
    return new ResponseEntity<>(latest, HttpStatus.OK);
  }
}
