package nl.gerimedica.assignment.enums;

public enum Reason {
  EXAMINATION("Examination"),
  CHECKUP("Checkup"),
  FOLLOWUP("Follow-up"),
  XRAY("X-Ray");

  private String reason;

  Reason(String reason) {
    this.reason = reason;
  }

  public String getReason() {
    return reason;
  }

  public static Reason getReasonFromString(String reasonKeyword) {
    Reason reasonMatched = EXAMINATION;
    for (Reason reason : Reason.values()) {
      if (reason.getReason().equals(reasonKeyword)) {
        reasonMatched = reason;
        break;
      }
    }
    return reasonMatched;
  }
}
