package nl.gerimedica.assignment.repository;

import java.util.List;
import nl.gerimedica.assignment.repository.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

  Patient findBySsn(String ssn);

}
