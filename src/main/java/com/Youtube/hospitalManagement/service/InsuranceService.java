package com.Youtube.hospitalManagement.service;

import com.Youtube.hospitalManagement.entity.Insurance;
import com.Youtube.hospitalManagement.entity.Patient;
import com.Youtube.hospitalManagement.repository.InsuranceRepo;
import com.Youtube.hospitalManagement.repository.PatientRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepo insuranceRepo;
    private final PatientRepo patientRepo;

    @Transactional
    public Patient assignInsuranceToPatient(
            Insurance insurance,
            Long patientId
    ) {

        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId));

        patient.setInsurance(insurance);
        // Maintain bidirectional relationship
        insurance.setPatient(patient);
        return patient;
    }

    @Transactional
    public Patient disassociateInsuranceFromPatient(Long patientId) {

        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId));

        patient.setInsurance(null);
        return patient;
    }


}