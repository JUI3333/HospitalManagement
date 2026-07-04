package com.Youtube.hospitalManagement.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.Youtube.hospitalManagement.entity.Patient;
import com.Youtube.hospitalManagement.repository.PatientRepo;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepo patientRepository;

    @Transactional
    public Patient getPatientById(Long Id){
        Patient p1 = patientRepository.findById(Id).orElseThrow();
        Patient p2 = patientRepository.findById(Id).orElseThrow();

        System.out.println(p1 == p2);

        p1.setName("Lucky!");

        return p1;
    }
}
