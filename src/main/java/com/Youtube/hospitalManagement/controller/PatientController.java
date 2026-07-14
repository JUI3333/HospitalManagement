package com.Youtube.hospitalManagement.controller;

import com.Youtube.hospitalManagement.dto.PatientResponseDto;
import com.Youtube.hospitalManagement.entity.Patient;
import com.Youtube.hospitalManagement.entity.type.BloodGroupType;
import com.Youtube.hospitalManagement.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// All routes start with /patients
// WebSecurityConfig: /patients/** → authenticated()
// Any logged-in user (PATIENT, DOCTOR, ADMIN) can access these

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    // Controller talks ONLY to Service — never to Repository directly
    private final PatientService patientService;

    // ── CREATE ────────────────────────────────────────────────────────
    // POST /patients
    // Body: { "name": "Raj", "email": "raj@gmail.com",
    //         "gender": "MALE", "birthDate": "2000-05-20",
    //         "bloodGroup": "O_POSITIVE" }
    // Returns: PatientResponseDto with generated ID
    // HTTP 201 CREATED — new resource was created
    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(
            @RequestBody Patient patient) {

        PatientResponseDto created = patientService.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── READ ALL ──────────────────────────────────────────────────────
    // GET /patients
    // Returns: List of all patients with their appointments
    // Uses LEFT JOIN FETCH internally — no N+1 problem
    @GetMapping
    public ResponseEntity<List<PatientResponseDto>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // ── READ ONE ──────────────────────────────────────────────────────
    // GET /patients/{id}
    // Example: GET /patients/1
    // Returns: Single patient with appointments and insurance
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatientById(
            @PathVariable Long id) {

        PatientResponseDto patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    // ── UPDATE ────────────────────────────────────────────────────────
    // PUT /patients/{id}
    // Body: only the fields you want to change
    // Example: { "name": "Raj Updated" } — only name changes
    // Smart update — null fields are ignored in service layer
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable Long id,
            @RequestBody Patient patient) {

        PatientResponseDto updated = patientService.updatePatient(id, patient);
        return ResponseEntity.ok(updated);
    }

    // ── DELETE ────────────────────────────────────────────────────────
    // DELETE /patients/{id}
    // Cascade deletes appointments too (CascadeType.REMOVE)
    // Cascade deletes insurance too (orphanRemoval=true)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatient(
            @PathVariable Long id) {

        String message = patientService.deletePatient(id);
        return ResponseEntity.ok(message);
    }

    // ── SEARCH BY BLOOD GROUP ─────────────────────────────────────────
    // GET /patients/blood-group/O_POSITIVE
    // Uses @Query from PatientRepo
    // BloodGroupType is an Enum — Spring converts string to enum automatically
    @GetMapping("/blood-group/{bloodGroup}")
    public ResponseEntity<List<PatientResponseDto>> getPatientsByBloodGroup(
            @PathVariable BloodGroupType bloodGroup) {

        return ResponseEntity.ok(
                patientService.getPatientsByBloodGroup(bloodGroup));
    }

    // ── SEARCH BY NAME ────────────────────────────────────────────────
    // GET /patients/search?query=Di
    // Finds patients whose name CONTAINS "Di" → "Diya", "Dishant"
    // Results ordered by ID descending (newest first)
    @GetMapping("/search")
    public ResponseEntity<List<PatientResponseDto>> searchPatients(
            @RequestParam String query) {

        return ResponseEntity.ok(
                patientService.searchPatientsByName(query));
    }
}