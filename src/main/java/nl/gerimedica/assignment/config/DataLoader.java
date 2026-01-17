package nl.gerimedica.assignment.config;

import nl.gerimedica.assignment.enums.Reason;
import nl.gerimedica.assignment.repository.AppointmentRepository;
import nl.gerimedica.assignment.repository.PatientRepository;
import nl.gerimedica.assignment.repository.entity.Appointment;
import nl.gerimedica.assignment.repository.entity.Patient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadSampleData(PatientRepository patientRepository,
            AppointmentRepository appointmentRepository) {
        return args -> {
            // Clear existing data
            appointmentRepository.deleteAll();
            patientRepository.deleteAll();

            // Create sample patients
            Patient patient1 = new Patient("John Doe", "123456789");
            Patient patient2 = new Patient("Jane Smith", "987654321");
            Patient patient3 = new Patient("Bob Johnson", "555666777");
            Patient patient4 = new Patient("Alice Williams", "111222333");
            Patient patient5 = new Patient("Charlie Brown", "444555666");

            patientRepository.save(patient1);
            patientRepository.save(patient2);
            patientRepository.save(patient3);
            patientRepository.save(patient4);
            patientRepository.save(patient5);

            // Create sample appointments for patient 1 (John Doe)
            appointmentRepository.save(new Appointment(
                    Reason.CHECKUP,
                    LocalDate.of(2025, 2, 1),
                    patient1));
            appointmentRepository.save(new Appointment(
                    Reason.FOLLOWUP,
                    LocalDate.of(2025, 2, 15),
                    patient1));
            appointmentRepository.save(new Appointment(
                    Reason.XRAY,
                    LocalDate.of(2025, 3, 1),
                    patient1));

            // Create sample appointments for patient 2 (Jane Smith)
            appointmentRepository.save(new Appointment(
                    Reason.CHECKUP,
                    LocalDate.of(2025, 1, 20),
                    patient2));
            appointmentRepository.save(new Appointment(
                    Reason.EXAMINATION,
                    LocalDate.of(2025, 2, 10),
                    patient2));

            // Create sample appointments for patient 3 (Bob Johnson)
            appointmentRepository.save(new Appointment(
                    Reason.EXAMINATION,
                    LocalDate.of(2025, 1, 25),
                    patient3));
            appointmentRepository.save(new Appointment(
                    Reason.CHECKUP,
                    LocalDate.of(2025, 3, 15),
                    patient3));
            appointmentRepository.save(new Appointment(
                    Reason.FOLLOWUP,
                    LocalDate.of(2025, 4, 1),
                    patient3));

            // Create sample appointments for patient 4 (Alice Williams)
            appointmentRepository.save(new Appointment(
                    Reason.XRAY,
                    LocalDate.of(2025, 2, 5),
                    patient4));

            // Create sample appointments for patient 5 (Charlie Brown)
            appointmentRepository.save(new Appointment(
                    Reason.CHECKUP,
                    LocalDate.of(2025, 1, 15),
                    patient5));
            appointmentRepository.save(new Appointment(
                    Reason.XRAY,
                    LocalDate.of(2025, 2, 20),
                    patient5));
            appointmentRepository.save(new Appointment(
                    Reason.EXAMINATION,
                    LocalDate.of(2025, 3, 10),
                    patient5));

            System.out.println("✅ Sample data loaded successfully!");
            System.out.println("📊 Created 5 patients and 13 appointments");
            System.out.println();
            System.out.println("Sample Patients:");
            System.out.println("  - John Doe (SSN: 123456789) - 3 appointments");
            System.out.println("  - Jane Smith (SSN: 987654321) - 2 appointments");
            System.out.println("  - Bob Johnson (SSN: 555666777) - 3 appointments");
            System.out.println("  - Alice Williams (SSN: 111222333) - 1 appointment");
            System.out.println("  - Charlie Brown (SSN: 444555666) - 3 appointments");
            System.out.println();
            System.out.println("🔗 Access Swagger UI at: http://localhost:8080/swagger-ui.html");
        };
    }
}
