package nl.gerimedica.assignment.service.dto;

import java.time.LocalDate;
import nl.gerimedica.assignment.repository.entity.Appointment;

public record AppointmentDTO(String ssn, LocalDate date) {

  public static AppointmentDTO toAppointmentDTO(Appointment appointment) {
    return new AppointmentDTO(appointment.patient.ssn, appointment.date);
  }
}
