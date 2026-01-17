package nl.gerimedica.assignment.exception;

public class PatientNotFoundException extends RuntimeException {

  private String message ;

  public PatientNotFoundException(String ssn) {
    this.message = String.format("Patient with SSN %s not found", ssn);
  }

  public String getMessage() {
    return message;
  }
}
