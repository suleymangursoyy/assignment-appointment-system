package nl.gerimedica.assignment.repository;

import java.util.List;
import nl.gerimedica.assignment.repository.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  List<Appointment> findByReason(String name);
}
