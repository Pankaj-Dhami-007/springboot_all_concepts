package com.dhami.hospital.management.System;

import com.dhami.hospital.management.System.entity.Patient;
import com.dhami.hospital.management.System.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PatientTests {

    @Autowired
    private PatientRepository patientRepository;


    @Test
    public void testPatientRepository() {

        List<Patient> patientList = patientRepository.findAll();
        System.out.println(patientList);

    }
}
