package com.Youtube.hospitalManagement.service;

import com.Youtube.hospitalManagement.dto.DoctorResponseDto;
import com.Youtube.hospitalManagement.entity.Doctor;
import com.Youtube.hospitalManagement.repository.DoctorRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepo doctorRepo;

    // ── HELPER — Convert Doctor Entity → DoctorResponseDto ───────────
    // Same pattern as PatientService.toDto()
    // Prevents circular references:
    // Doctor → Appointment → Doctor → ... (infinite loop)
    // Instead: Doctor → AppointmentSummary (safe, flat object)
    private DoctorResponseDto toDto(Doctor doctor) {
        return DoctorResponseDto.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .specialization(doctor.getSpecialization())
                .email(doctor.getEmail())

                // Convert Set<Department> → List<String> (just names)
                // We only need department names in the response
                // Full Department objects would cause too much nesting
                .departmentNames(doctor.getDepartments().stream()
                        .map(department -> department.getName())
                        .toList())

                // Convert List<Appointment> → List<AppointmentSummary>
                // Each summary has just id, time, reason, patientName
                .appointments(doctor.getAppointments().stream()
                        .map(appointment -> DoctorResponseDto.AppointmentSummary.builder()
                                .id(appointment.getId())
                                .appointmentTime(appointment.getAppointmentTime())
                                .reason(appointment.getReason())
                                // Get patient name safely
                                .patientName(appointment.getPatient() != null ?
                                        appointment.getPatient().getName() : null)
                                .build())
                        .toList())
                .build();
    }

    // ── CREATE — Add new doctor ───────────────────────────────────────
    @Transactional
    public DoctorResponseDto createDoctor(Doctor doctor) {

        // Validate no ID on new doctor
        if (doctor.getId() != null) {
            throw new IllegalArgumentException(
                    "New doctor should not have an ID"
            );
        }

        // Check email uniqueness before saving
        // Doctor.email has @Column(unique=true)
        // But giving a clear message is better than a DB constraint error
        if (doctor.getEmail() != null &&
                doctorRepo.findByEmail(doctor.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Doctor already exists with email: " + doctor.getEmail()
            );
        }

        Doctor saved = doctorRepo.save(doctor);
        return toDto(saved);
    }

    // ── READ ALL — Get every doctor ───────────────────────────────────
    @Transactional
    // @Transactional needed for LAZY loaded departments + appointments
    public List<DoctorResponseDto> getAllDoctors() {
        return doctorRepo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ── READ ONE — Get doctor by ID ───────────────────────────────────
    @Transactional
    public DoctorResponseDto getDoctorById(Long id) {
        Doctor doctor = doctorRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Doctor not found with id: " + id
                ));
        return toDto(doctor);
    }

    // ── UPDATE — Modify existing doctor ──────────────────────────────
    @Transactional
    public DoctorResponseDto updateDoctor(Long id, Doctor updatedData) {

        Doctor existing = doctorRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Doctor not found with id: " + id
                ));

        // Smart update — only change what's provided
        if (updatedData.getName() != null)
            existing.setName(updatedData.getName());

        if (updatedData.getSpecialization() != null)
            existing.setSpecialization(updatedData.getSpecialization());

        if (updatedData.getEmail() != null)
            existing.setEmail(updatedData.getEmail());

        Doctor saved = doctorRepo.save(existing);
        return toDto(saved);
    }

    // ── DELETE — Remove doctor by ID ─────────────────────────────────
    @Transactional
    public String deleteDoctor(Long id) {

        Doctor doctor = doctorRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Doctor not found with id: " + id
                ));

        doctorRepo.delete(doctor);
        return "Doctor with id " + id + " deleted successfully";
    }

    // ── SEARCH — Find by specialization ──────────────────────────────
    @Transactional
    public List<DoctorResponseDto> getDoctorsBySpecialization(String specialization) {
        return doctorRepo.findBySpecialization(specialization)
                .stream()
                .map(this::toDto)
                .toList();
    }
}