package com.Youtube.hospitalManagement.controller;

import com.Youtube.hospitalManagement.dto.BloodGroupCountResponseEntity;
import com.Youtube.hospitalManagement.dto.DoctorResponseDto;
import com.Youtube.hospitalManagement.dto.PatientResponseDto;
import com.Youtube.hospitalManagement.entity.type.BloodGroupType;
import com.Youtube.hospitalManagement.repository.PatientRepo;
import com.Youtube.hospitalManagement.service.DoctorService;
import com.Youtube.hospitalManagement.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// AdminController handles admin-only operations
// WebSecurityConfig: /admin/** → hasRole("ADMIN")
// ONLY users with role="ADMIN" can access these

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PatientService patientService;
    private final DoctorService doctorService;

    // PatientRepo used directly for admin-level queries
    // (pagination, blood group stats) — advanced queries not in service yet
    private final PatientRepo patientRepo;

    // ── PATIENT MANAGEMENT ────────────────────────────────────────────

    // GET ALL PATIENTS (PAGINATED)
    // GET /admin/patients?page=0&size=5&sortBy=name
    // Admin can view patients with pagination and sorting
    // page=0 → first page (0-indexed)
    // size=5 → 5 patients per page
    // sortBy=name → sorted alphabetically by name
    @GetMapping("/patients")
    public ResponseEntity<Page<PatientResponseDto>> getAllPatientsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        // PageRequest.of(page, size, sort) — builds pagination instruction
        Page<PatientResponseDto> patients = patientRepo
                .findAllPatients(PageRequest.of(page, size, Sort.by(sortBy)))
                .map(patient -> patientService.getPatientById(patient.getId()));
        // .map() on Page converts each Patient entity → PatientResponseDto

        return ResponseEntity.ok(patients);
    }

    // BLOOD GROUP STATISTICS
    // GET /admin/patients/blood-group-stats
    // Returns count of patients per blood group
    // Example response:
    // [ { "bloodGroup": "O_POSITIVE", "count": 2 },
    //   { "bloodGroup": "A_POSITIVE", "count": 2 } ]
    @GetMapping("/patients/blood-group-stats")
    public ResponseEntity<List<BloodGroupCountResponseEntity>> getBloodGroupStats() {

        return ResponseEntity.ok(
                patientRepo.countEachBloodGroupType());
    }

    // UPDATE PATIENT NAME (BULK/DIRECT)
    // PATCH /admin/patients/{id}/name?newName=Aarav Wagh
    // Uses @Modifying @Query from PatientRepo
    // Direct SQL update — faster than fetch-modify-save pattern
    // Returns number of rows updated (should be 1)
    @PatchMapping("/patients/{id}/name")
    public ResponseEntity<String> updatePatientName(
            @PathVariable Long id,
            @RequestParam String newName) {

        int rowsUpdated = patientRepo.updateNameWithId(newName, id);

        if (rowsUpdated == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                "Patient name updated successfully. Rows affected: " + rowsUpdated);
    }

    // DELETE PATIENT (ADMIN ONLY)
    // DELETE /admin/patients/{id}
    // Admin can delete any patient
    @DeleteMapping("/patients/{id}")
    public ResponseEntity<String> deletePatient(
            @PathVariable Long id) {

        return ResponseEntity.ok(patientService.deletePatient(id));
    }

    // ── DOCTOR MANAGEMENT ─────────────────────────────────────────────

    // GET ALL DOCTORS
    // GET /admin/doctors
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorResponseDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // DELETE DOCTOR (ADMIN ONLY)
    // DELETE /admin/doctors/{id}
    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<String> deleteDoctor(
            @PathVariable Long id) {

        return ResponseEntity.ok(doctorService.deleteDoctor(id));
    }

    // ── SEARCH ────────────────────────────────────────────────────────

    // SEARCH PATIENTS BY BLOOD GROUP
    // GET /admin/patients/blood-group/O_POSITIVE
    @GetMapping("/patients/blood-group/{bloodGroup}")
    public ResponseEntity<List<PatientResponseDto>> getPatientsByBloodGroup(
            @PathVariable BloodGroupType bloodGroup) {

        return ResponseEntity.ok(
                patientService.getPatientsByBloodGroup(bloodGroup));
    }

    // SEARCH PATIENTS BY NAME
    // GET /admin/patients/search?query=Di
    @GetMapping("/patients/search")
    public ResponseEntity<List<PatientResponseDto>> searchPatients(
            @RequestParam String query) {

        return ResponseEntity.ok(
                patientService.searchPatientsByName(query));
    }
}