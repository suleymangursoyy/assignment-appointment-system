package nl.gerimedica.assignment.repository;

import java.util.List;
import nl.gerimedica.assignment.enums.Reason;
import nl.gerimedica.assignment.repository.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  @Query("""
      select a from Appointment a
            join fetch a.patient
            where a.reason = :reason      
      """)
  List<Appointment> findByReason(@Param("reason") Reason reason);

  @Query("""
      select a from Appointment a
            where a.patient.ssn = :ssn
                  order by a.date desc 
                        limit 1
      """)
  Appointment findBySsnOrderByDateDesc(@Param("ssn") String ssn);
}
