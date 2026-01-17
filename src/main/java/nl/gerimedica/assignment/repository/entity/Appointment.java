package nl.gerimedica.assignment.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;
import nl.gerimedica.assignment.enums.Reason;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull(message = "Appointment reason is required")
    public Reason reason;

    @NotNull(message = "Appointment date is required")
    public LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @NotNull(message = "Appointment must be linked to a patient")
    public Patient patient;

    public Appointment() {
    }

    public Appointment(Reason reason, LocalDate date, Patient patient) {
        this.reason = reason;
        this.date = date;
        this.patient = patient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Appointment that))
            return false;
        return Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reason);
    }
}
