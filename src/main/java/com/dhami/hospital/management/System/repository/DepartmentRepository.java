package com.dhami.hospital.management.System.repository;

import com.dhami.hospital.management.System.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
