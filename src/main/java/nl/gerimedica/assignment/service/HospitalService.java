package nl.gerimedica.assignment.service;

import lombok.extern.slf4j.Slf4j;
import nl.gerimedica.assignment.enums.Reason;
import nl.gerimedica.assignment.util.HospitalUtils;
import nl.gerimedica.assignment.repository.AppointmentRepository;
import nl.gerimedica.assignment.repository.PatientRepository;
import nl.gerimedica.assignment.repository.entity.Appointment;
import nl.gerimedica.assignment.repository.entity.Patient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static nl.gerimedica.assignment.enums.Reason.getReasonFromString;

@Service
@Slf4j
public class HospitalService {

  private final PatientRepository patientRepo;

  private final AppointmentRepository appointmentRepo;

  private final HospitalUtils hospitalUtils;

  public HospitalService(PatientRepository patientRepo, AppointmentRepository appointmentRepo,
                         HospitalUtils hospitalUtils) {
    this.patientRepo = patientRepo;
    this.appointmentRepo = appointmentRepo;
    this.hospitalUtils = hospitalUtils;
  }

  public List<Appointment> bulkCreateAppointments(
      String patientName,
      String ssn,
      List<String> reasons,
      List<String> dates
                                                 ) {
    Patient patient = findPatientBySSN(ssn);
    if (patient == null) {
      log.info("Creating new patient with SSN: {}", ssn);
      patient = new Patient(patientName, ssn);
      savePatient(patient);
    } else {
      log.info("Existing patient patient, SSN: {}", patient.ssn);
    }

    List<Appointment> createdAppointments = new ArrayList<>();
    int loopSize = Math.min(reasons.size(), dates.size());
    for (int i = 0; i < loopSize; i++) {
      Reason reason = getReasonFromString(reasons.get(i));
      String date = dates.get(i);
      Appointment appt = new Appointment(reason, date, patient);
      createdAppointments.add(appt);
    }

    appointmentRepo.saveAll(createdAppointments);

    for (Appointment appt : createdAppointments) {
      log.info("Created appointment for reason: {} [Date: {}] [Patient SSN: {}]", appt.reason, appt.date,
               appt.patient.ssn);
    }

    HospitalUtils.recordUsage("Bulk create appointments");

    return createdAppointments;
  }

  public Patient findPatientBySSN(String ssn) {
    return patientRepo.findBySsn(ssn);
  }

  @Transactional
  void savePatient(Patient patient) {
    patientRepo.save(patient);
  }

  public List<Appointment> getAppointmentsByReason(Reason reasonKeyword) {
    // fix
    List<Appointment> matchedAppointments = appointmentRepo.findByReason(reasonKeyword.name());

    hospitalUtils.recordUsage("Get appointments by reason");

    return matchedAppointments;
  }

  public void deleteAppointmentsBySSN(String ssn) {
    Patient patient = findPatientBySSN(ssn);
    if (patient == null) {
      return;
    }
    List<Appointment> appointments = patient.appointments;
    appointmentRepo.deleteAll(appointments);
  }

  public Appointment findLatestAppointmentBySSN(String ssn) {
    Patient patient = findPatientBySSN(ssn);
    if (patient == null || patient.appointments == null || patient.appointments.isEmpty()) {
      return null;
    }

    Appointment latest = null;
    for (Appointment appt : patient.appointments) {
      if (latest == null) {
        latest = appt;
      } else {
        if (appt.date.compareTo(latest.date) > 0) {
          latest = appt;
        }
      }
    }

    return latest;
  }
}
