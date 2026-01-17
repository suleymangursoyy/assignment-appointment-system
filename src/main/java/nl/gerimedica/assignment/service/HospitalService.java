package nl.gerimedica.assignment.service;

import java.time.LocalDate;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import nl.gerimedica.assignment.exception.PatientNotFoundException;
import nl.gerimedica.assignment.enums.Reason;
import nl.gerimedica.assignment.service.dto.AppointmentDTO;
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
import static nl.gerimedica.assignment.service.dto.AppointmentDTO.toAppointmentDTO;

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

  public List<AppointmentDTO> bulkCreateAppointments(
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
      LocalDate date = LocalDate.parse(dates.get(i));
      Appointment appt = new Appointment(reason, date, patient);
      createdAppointments.add(appt);
    }

    List<Appointment> appointments = appointmentRepo.saveAll(createdAppointments);

    var appointmentDTOs = appointments.stream().map(AppointmentDTO::toAppointmentDTO).toList();

    for (Appointment appt : createdAppointments) {
      log.info("Created appointment for reason: {} [Date: {}] [Patient SSN: {}]", appt.reason, appt.date,
               appt.patient.ssn);
    }

    HospitalUtils.recordUsage("Bulk create appointments");

    return appointmentDTOs;
  }

  public Patient findPatientBySSN(String ssn) {
    return patientRepo.findBySsn(ssn);
  }

  @Transactional
  void savePatient(Patient patient) {
    patientRepo.save(patient);
  }

  public List<AppointmentDTO> getAppointmentsByReason(Reason reasonKeyword) {
    // In case of  very high records can result maximum heap usage, and this cause application slowness maybe even crush
    List<Appointment> matchedAppointments = appointmentRepo.findByReason(reasonKeyword);

    hospitalUtils.recordUsage("Get appointments by reason");

    var appointmentDTOs = matchedAppointments.stream().map(AppointmentDTO::toAppointmentDTO).toList();
    return appointmentDTOs;
  }

  public void deleteAppointmentsBySSN(String ssn) {
    Patient patient = findPatientBySSN(ssn);
    if (Objects.isNull(patient)) {
      throw new PatientNotFoundException(ssn);
    }
    List<Appointment> appointments = patient.appointments;
    appointmentRepo.deleteAll(appointments);
  }

  public AppointmentDTO findLatestAppointmentBySSN(String ssn) {
    Patient patient = findPatientBySSN(ssn);
    if (Objects.isNull(patient)) {
      throw new PatientNotFoundException(ssn);
    }

    // TODO we need eaxct appointment date to get best result.
    // Getting all patient appointments is unnecessary, may due performance problems.This type queries creates N+1 query issue
    Appointment appointment = appointmentRepo.findBySsnOrderByDateDesc(ssn);
    if (Objects.isNull(appointment)) {
      throw new PatientNotFoundException(ssn);
    }

    return toAppointmentDTO(appointment);
  }
}
