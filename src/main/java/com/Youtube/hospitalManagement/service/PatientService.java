package com.Youtube.hospitalManagement.service;

import com.Youtube.hospitalManagement.dto.PatientResponseDto;
import com.Youtube.hospitalManagement.entity.Patient;
import com.Youtube.hospitalManagement.entity.type.BloodGroupType;
import com.Youtube.hospitalManagement.repository.PatientRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepo patientRepository;

    // ── PRIVATE HELPER — Entity → DTO conversion ──────────────────────
    // Controllers should never receive raw Entity objects
    // Raw entities can cause:
    // 1. Circular reference JSON error
    //    (Patient → Appointment → Patient → ... infinite loop)
    // 2. Sensitive data exposure
    // 3. Lazy loading exceptions outside transaction
    // DTO is the safe, clean version we send to controllers
    private PatientResponseDto toDto(Patient patient) {
        return PatientResponseDto.builder()
                .id(patient.getId())
                .name(patient.getName())
                .email(patient.getEmail())
                .gender(patient.getGender())
                .birthDate(patient.getBirthDate())
                .bloodGroup(patient.getBloodGroup())
                .createdAt(patient.getCreatedAt())

                // Insurance → InsuranceSummary
                // null check — patient may not have insurance yet
                .insurance(patient.getInsurance() != null ?
                        PatientResponseDto.InsuranceSummary.builder()
                                .id(patient.getInsurance().getId())
                                .policyNumber(patient.getInsurance().getPolicyNumber())
                                .provider(patient.getInsurance().getProvider())
                                .validUntil(patient.getInsurance().getValidUntil())
                                .build()
                        : null)

                // List<Appointment> → List<AppointmentSummary>
                // each appointment converted to a flat summary object
                // avoids Appointment → Doctor → Appointment circular loop
                .appointments(patient.getAppointments().stream()
                        .map(appointment ->
                                PatientResponseDto.AppointmentSummary.builder()
                                        .id(appointment.getId())
                                        .appointmentTime(appointment.getAppointmentTime())
                                        .reason(appointment.getReason())
                                        .doctorName(appointment.getDoctor() != null ?
                                                appointment.getDoctor().getName() : null)
                                        .build())
                        .toList())
                .build();
    }

    // ── YOUR ORIGINAL METHOD — Kept exactly as you wrote it ───────────
    // Demonstrates 2 important Hibernate concepts:
    //
    // CONCEPT 1 — First Level Cache (Persistence Context):
    //   findById() called TWICE with same ID
    //   Second call does NOT hit the database again
    //   Returns same object from Persistence Context cache
    //   p1 == p2 → prints TRUE (same object in memory)
    //
    // CONCEPT 2 — Dirty Checking:
    //   p1.setName("Lucky!") changes the object
    //   Since p1 is PERSISTENT (inside @Transactional)
    //   Hibernate detects this change automatically
    //   At end of @Transactional → runs UPDATE SQL automatically
    //   No explicit save() needed!
    @Transactional
    public PatientResponseDto getPatientById(Long id) {

        // First call → hits PostgreSQL → stores in Persistence Context
        Patient p1 = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient not found with id: " + id
                ));

        // Second call → finds in Persistence Context → NO DB query!
        Patient p2 = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient not found with id: " + id
                ));

        // Proves First Level Cache — same object reference
        System.out.println("Same object from cache: " + (p1 == p2));

        // Dirty Checking demo — Hibernate auto-saves this at end of transaction
        p1.setName("Lucky!");

        // Convert to DTO before returning
        // Can't return raw Patient — causes circular JSON error
        return toDto(p1);
    }

    // ── CREATE — Save new patient to database ─────────────────────────
    // Patient object comes from controller via @RequestBody
    // We validate, save, convert to DTO, return
    @Transactional
    public PatientResponseDto createPatient(Patient patient) {

        // Reject if ID already set — this should be a NEW patient
        // If ID is passed, JPA's save() would try to UPDATE, not INSERT
        if (patient.getId() != null) {
            throw new IllegalArgumentException(
                    "New patient must not have an ID — ID is auto-generated"
            );
        }

        // TRANSIENT → PERSISTENT
        // Before save: patient has no ID, DB doesn't know it
        // After save:  patient has auto-generated ID, stored in PostgreSQL
        Patient saved = patientRepository.save(patient);

        return toDto(saved);
    }

    // ── READ ALL — Get all patients ───────────────────────────────────
    // Uses LEFT JOIN FETCH from PatientRepo
    // Solves N+1 problem:
    //   Without JOIN FETCH: 1 query for patients + 1 per patient for appointments
    //   With JOIN FETCH: 1 single query gets everything
    @Transactional
    public List<PatientResponseDto> getAllPatients() {

        // findAllPatientWithAppointments() already exists in your PatientRepo:
        // @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.appointments")
        return patientRepository.findAllPatientWithAppointments()
                .stream()
                .map(this::toDto)
                // this::toDto = method reference
                // same as writing: .map(patient -> toDto(patient))
                .toList();
    }

    // ── UPDATE — Modify existing patient ─────────────────────────────
    // Smart partial update — only updates fields that are not null
    // Example: client sends only { "email": "new@email.com" }
    // → only email changes, name/gender/etc stay the same
    @Transactional
    public PatientResponseDto updatePatient(Long id, Patient updatedData) {

        // Fetch existing — throws clear error if not found
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient not found with id: " + id
                ));

        // Only update fields that client actually provided
        if (updatedData.getName() != null)
            existing.setName(updatedData.getName());

        if (updatedData.getEmail() != null)
            existing.setEmail(updatedData.getEmail());

        if (updatedData.getGender() != null)
            existing.setGender(updatedData.getGender());

        if (updatedData.getBirthDate() != null)
            existing.setBirthDate(updatedData.getBirthDate());

        if (updatedData.getBloodGroup() != null)
            existing.setBloodGroup(updatedData.getBloodGroup());

        // existing is PERSISTENT inside @Transactional
        // Hibernate would auto-save via dirty checking
        // But explicit save() is clearer and returns the saved state
        Patient saved = patientRepository.save(existing);
        return toDto(saved);
    }

    // ── DELETE — Remove patient by ID ─────────────────────────────────
    // CascadeType.REMOVE on Patient.appointments
    // → when patient deleted, all their appointments deleted too
    // orphanRemoval on Patient.insurance
    // → when patient deleted, their insurance deleted too
    @Transactional
    public String deletePatient(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient not found with id: " + id
                ));

        patientRepository.delete(patient);

        return "Patient with id " + id + " deleted successfully";
    }

    // ── SEARCH — By blood group ───────────────────────────────────────
    // Uses your existing @Query in PatientRepo:
    // @Query("SELECT p FROM Patient p WHERE p.bloodGroup = ?1")
    @Transactional
    public List<PatientResponseDto> getPatientsByBloodGroup(BloodGroupType bloodGroup) {

        return patientRepository.findByBloodGroup(bloodGroup)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ── SEARCH — By name containing ───────────────────────────────────
    // Uses your existing derived query in PatientRepo:
    // findByNameContainingOrderByIdDesc(String query)
    // Example: searchPatientsByName("Di") → finds "Diya", "Dishant"
    @Transactional
    public List<PatientResponseDto> searchPatientsByName(String query) {

        return patientRepository.findByNameContainingOrderByIdDesc(query)
                .stream()
                .map(this::toDto)
                .toList();
    }
}