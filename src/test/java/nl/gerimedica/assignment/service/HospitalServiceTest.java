package nl.gerimedica.assignment.service;

import nl.gerimedica.assignment.enums.Reason;
import nl.gerimedica.assignment.exception.PatientNotFoundException;
import nl.gerimedica.assignment.repository.AppointmentRepository;
import nl.gerimedica.assignment.repository.PatientRepository;
import nl.gerimedica.assignment.repository.entity.Appointment;
import nl.gerimedica.assignment.repository.entity.Patient;
import nl.gerimedica.assignment.service.dto.AppointmentDTO;
import nl.gerimedica.assignment.util.HospitalUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private HospitalUtils hospitalUtils;

    @InjectMocks
    private HospitalService hospitalService;

    private Patient testPatient;
    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        testPatient = new Patient("John Doe", "123456789");
        testPatient.id = 1L;

        testAppointment = new Appointment(Reason.CHECKUP, LocalDate.of(2025, 2, 1), testPatient);
        testAppointment.id = 1L;
    }

    @Test
    void bulkCreateAppointments_WhenPatientExists_ShouldCreateAppointments() {
        // Arrange
        String ssn = "123456789";
        String patientName = "John Doe";
        List<String> reasons = Arrays.asList("Checkup", "Follow-up");
        List<String> dates = Arrays.asList("2025-02-01", "2025-02-15");

        when(patientRepository.findBySsn(ssn)).thenReturn(testPatient);

        List<Appointment> savedAppointments = Arrays.asList(
                new Appointment(Reason.CHECKUP, LocalDate.of(2025, 2, 1), testPatient),
                new Appointment(Reason.FOLLOWUP, LocalDate.of(2025, 2, 15), testPatient));
        when(appointmentRepository.saveAll(any())).thenReturn(savedAppointments);

        // Act
        List<AppointmentDTO> result = hospitalService.bulkCreateAppointments(patientName, ssn, reasons, dates);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(patientRepository, times(1)).findBySsn(ssn);
        verify(patientRepository, never()).save(any(Patient.class));
        verify(appointmentRepository, times(1)).saveAll(any());
    }

    @Test
    void bulkCreateAppointments_WhenPatientDoesNotExist_ShouldCreatePatientAndAppointments() {
        // Arrange
        String ssn = "987654321";
        String patientName = "Jane Smith";
        List<String> reasons = Arrays.asList("Checkup");
        List<String> dates = Arrays.asList("2025-02-01");

        when(patientRepository.findBySsn(ssn)).thenReturn(null);

        List<Appointment> savedAppointments = Arrays.asList(
                new Appointment(Reason.CHECKUP, LocalDate.of(2025, 2, 1), new Patient(patientName, ssn)));
        when(appointmentRepository.saveAll(any())).thenReturn(savedAppointments);

        // Act
        List<AppointmentDTO> result = hospitalService.bulkCreateAppointments(patientName, ssn, reasons, dates);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository, times(1)).findBySsn(ssn);
        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(appointmentRepository, times(1)).saveAll(any());
    }

    @Test
    void bulkCreateAppointments_WhenReasonsAndDatesHaveDifferentSizes_ShouldCreateMinimumNumber() {
        // Arrange
        String ssn = "123456789";
        String patientName = "John Doe";
        List<String> reasons = Arrays.asList("Checkup", "Follow-up", "X-Ray");
        List<String> dates = Arrays.asList("2025-02-01", "2025-02-15");

        when(patientRepository.findBySsn(ssn)).thenReturn(testPatient);

        List<Appointment> savedAppointments = Arrays.asList(
                new Appointment(Reason.CHECKUP, LocalDate.of(2025, 2, 1), testPatient),
                new Appointment(Reason.FOLLOWUP, LocalDate.of(2025, 2, 15), testPatient));
        when(appointmentRepository.saveAll(any())).thenReturn(savedAppointments);

        // Act
        List<AppointmentDTO> result = hospitalService.bulkCreateAppointments(patientName, ssn, reasons, dates);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Should create only 2 appointments (minimum of 3 and 2)
        verify(appointmentRepository, times(1)).saveAll(argThat(list -> list != null && ((List<?>) list).size() == 2));
    }

    @Test
    void findPatientBySSN_WhenPatientExists_ShouldReturnPatient() {
        // Arrange
        String ssn = "123456789";
        when(patientRepository.findBySsn(ssn)).thenReturn(testPatient);

        // Act
        Patient result = hospitalService.findPatientBySSN(ssn);

        // Assert
        assertNotNull(result);
        assertEquals(ssn, result.ssn);
        assertEquals("John Doe", result.name);
        verify(patientRepository, times(1)).findBySsn(ssn);
    }

    @Test
    void findPatientBySSN_WhenPatientDoesNotExist_ShouldReturnNull() {
        // Arrange
        String ssn = "999999999";
        when(patientRepository.findBySsn(ssn)).thenReturn(null);

        // Act
        Patient result = hospitalService.findPatientBySSN(ssn);

        // Assert
        assertNull(result);
        verify(patientRepository, times(1)).findBySsn(ssn);
    }

    @Test
    void getAppointmentsByReason_ShouldReturnMatchingAppointments() {
        // Arrange
        Reason reason = Reason.CHECKUP;
        List<Appointment> appointments = Arrays.asList(
                new Appointment(Reason.CHECKUP, LocalDate.of(2025, 2, 1), testPatient),
                new Appointment(Reason.CHECKUP, LocalDate.of(2025, 3, 1), testPatient));
        when(appointmentRepository.findByReason(reason)).thenReturn(appointments);

        // Act
        List<AppointmentDTO> result = hospitalService.getAppointmentsByReason(reason);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appointmentRepository, times(1)).findByReason(reason);
    }

    @Test
    void getAppointmentsByReason_WhenNoAppointmentsFound_ShouldReturnEmptyList() {
        // Arrange
        Reason reason = Reason.EXAMINATION;
        when(appointmentRepository.findByReason(reason)).thenReturn(new ArrayList<>());

        // Act
        List<AppointmentDTO> result = hospitalService.getAppointmentsByReason(reason);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(appointmentRepository, times(1)).findByReason(reason);
    }

    @Test
    void deleteAppointmentsBySSN_WhenPatientExists_ShouldDeleteAppointments() {
        // Arrange
        String ssn = "123456789";
        List<Appointment> appointments = Arrays.asList(testAppointment);
        testPatient.appointments = appointments;

        when(patientRepository.findBySsn(ssn)).thenReturn(testPatient);

        // Act
        hospitalService.deleteAppointmentsBySSN(ssn);

        // Assert
        verify(patientRepository, times(1)).findBySsn(ssn);
        verify(appointmentRepository, times(1)).deleteAll(appointments);
    }

    @Test
    void deleteAppointmentsBySSN_WhenPatientDoesNotExist_ShouldThrowException() {
        // Arrange
        String ssn = "999999999";
        when(patientRepository.findBySsn(ssn)).thenReturn(null);

        // Act & Assert
        assertThrows(
                PatientNotFoundException.class,
                () -> hospitalService.deleteAppointmentsBySSN(ssn));

        verify(patientRepository, times(1)).findBySsn(ssn);
    }

    @Test
    void findLatestAppointmentBySSN_WhenPatientAndAppointmentExist_ShouldReturnLatestAppointment() {
        // Arrange
        String ssn = "123456789";
        Appointment latestAppointment = new Appointment(Reason.FOLLOWUP, LocalDate.of(2025, 3, 1), testPatient);

        when(patientRepository.findBySsn(ssn)).thenReturn(testPatient);
        when(appointmentRepository.findBySsnOrderByDateDesc(ssn)).thenReturn(latestAppointment);

        // Act
        AppointmentDTO result = hospitalService.findLatestAppointmentBySSN(ssn);

        // Assert
        assertNotNull(result);
        assertEquals(LocalDate.of(2025, 3, 1), result.date());
        verify(patientRepository, times(1)).findBySsn(ssn);
        verify(appointmentRepository, times(1)).findBySsnOrderByDateDesc(ssn);
    }

    @Test
    void findLatestAppointmentBySSN_WhenPatientDoesNotExist_ShouldThrowException() {
        // Arrange
        String ssn = "999999999";
        when(patientRepository.findBySsn(ssn)).thenReturn(null);

        // Act & Assert
        PatientNotFoundException exception = assertThrows(
                PatientNotFoundException.class,
                () -> hospitalService.findLatestAppointmentBySSN(ssn));

        assertNotNull(exception);
        verify(patientRepository, times(1)).findBySsn(ssn);
        verify(appointmentRepository, never()).findBySsnOrderByDateDesc(anyString());
    }

    @Test
    void findLatestAppointmentBySSN_WhenNoAppointmentsExist_ShouldThrowException() {
        // Arrange
        String ssn = "123456789";
        when(patientRepository.findBySsn(ssn)).thenReturn(testPatient);
        when(appointmentRepository.findBySsnOrderByDateDesc(ssn)).thenReturn(null);

        // Act & Assert
        PatientNotFoundException exception = assertThrows(
                PatientNotFoundException.class,
                () -> hospitalService.findLatestAppointmentBySSN(ssn));

        assertNotNull(exception);
        verify(patientRepository, times(1)).findBySsn(ssn);
        verify(appointmentRepository, times(1)).findBySsnOrderByDateDesc(ssn);
    }

    @Test
    void savePatient_ShouldCallRepositorySave() {
        // Arrange
        Patient newPatient = new Patient("Test Patient", "111222333");

        // Act
        hospitalService.savePatient(newPatient);

        // Assert
        verify(patientRepository, times(1)).save(newPatient);
    }
}
