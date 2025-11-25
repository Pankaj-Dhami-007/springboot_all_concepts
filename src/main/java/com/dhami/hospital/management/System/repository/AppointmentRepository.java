package com.dhami.hospital.management.System.repository;

import com.dhami.hospital.management.System.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
