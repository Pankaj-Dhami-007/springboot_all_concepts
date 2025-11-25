package com.dhami.hospital.management.System.repository;

import com.dhami.hospital.management.System.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
