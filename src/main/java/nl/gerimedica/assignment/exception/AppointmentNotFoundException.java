package nl.gerimedica.assignment.exception;

public class AppointmentNotFoundException extends RuntimeException {

  private String message ;

  public AppointmentNotFoundException(String ssn) {
    this.message = String.format("Appointment not found with patient SSN:{}", ssn);
  }

  public String getMessage() {
    return message;
  }
}
