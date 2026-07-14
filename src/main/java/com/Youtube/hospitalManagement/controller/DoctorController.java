package com.Youtube.hospitalManagement.controller;

import com.Youtube.hospitalManagement.dto.DoctorResponseDto;
import com.Youtube.hospitalManagement.entity.Doctor;
import com.Youtube.hospitalManagement.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// All routes start with /doctors
// WebSecurityConfig: /doctors/** → hasAnyRole("DOCTOR", "ADMIN")
// Only DOCTOR and ADMIN roles can access these endpoints

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // ── CREATE ────────────────────────────────────────────────────────
    // POST /doctors
    // Body: { "name": "Dr. Nidhi", "specialization": "Cardiology",
    //         "email": "nidhi@hospital.com" }
    // Returns: DoctorResponseDto with generated ID
    @PostMapping
    public ResponseEntity<DoctorResponseDto> createDoctor(
            @RequestBody Doctor doctor) {

        DoctorResponseDto created = doctorService.createDoctor(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── READ ALL ──────────────────────────────────────────────────────
    // GET /doctors
    // Returns: All doctors with their appointments and department names
    @GetMapping
    public ResponseEntity<List<DoctorResponseDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // ── READ ONE ──────────────────────────────────────────────────────
    // GET /doctors/{id}
    // Example: GET /doctors/1
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> getDoctorById(
            @PathVariable Long id) {

        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    // ── UPDATE ────────────────────────────────────────────────────────
    // PUT /doctors/{id}
    // Body: only fields to update
    // Example: { "specialization": "Neurology" }
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> updateDoctor(
            @PathVariable Long id,
            @RequestBody Doctor doctor) {

        return ResponseEntity.ok(doctorService.updateDoctor(id, doctor));
    }

    // ── DELETE ────────────────────────────────────────────────────────
    // DELETE /doctors/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoctor(
            @PathVariable Long id) {

        return ResponseEntity.ok(doctorService.deleteDoctor(id));
    }

    // ── SEARCH BY SPECIALIZATION ──────────────────────────────────────
    // GET /doctors/specialization?query=Cardiology
    // Finds all doctors with that specialization
    @GetMapping("/specialization")
    public ResponseEntity<List<DoctorResponseDto>> getDoctorsBySpecialization(
            @RequestParam String query) {

        return ResponseEntity.ok(
                doctorService.getDoctorsBySpecialization(query));
    }
}