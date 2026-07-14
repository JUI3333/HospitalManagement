package com.Youtube.hospitalManagement.controller;

import com.Youtube.hospitalManagement.dto.AppointmentResponseDto;
import com.Youtube.hospitalManagement.dto.CreateAppointmentRequestDto;
import com.Youtube.hospitalManagement.entity.Appointment;
import com.Youtube.hospitalManagement.entity.Insurance;
import com.Youtube.hospitalManagement.entity.Patient;
import com.Youtube.hospitalManagement.service.AppointmentService;
import com.Youtube.hospitalManagement.service.InsuranceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// HospitalController handles:
// 1. Appointment operations (create, reassign)
// 2. Insurance operations (assign, remove)
// Routes: /hospital/**
// Access: any authenticated user

@RestController
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HospitalController {

    private final AppointmentService appointmentService;
    private final InsuranceService insuranceService;

    // ── APPOINTMENTS ──────────────────────────────────────────────────

    // CREATE APPOINTMENT
    // POST /hospital/appointments
    // Body: {
    //   "appointmentTime": "2025-07-10T10:30:00",
    //   "reason": "General Checkup",
    //   "doctorId": 1,
    //   "patientId": 2
    // }
    // Flow:
    // 1. Controller receives CreateAppointmentRequestDto
    // 2. Builds Appointment entity from dto fields
    // 3. Passes to AppointmentService with doctorId and patientId
    // 4. Service fetches Doctor + Patient, sets relationships, saves
    // 5. Returns AppointmentResponseDto
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @RequestBody CreateAppointmentRequestDto requestDto) {

        // Build Appointment entity from DTO
        // doctorId and patientId stay separate — service handles the lookup
        Appointment appointment = Appointment.builder()
                .appointmentTime(requestDto.getAppointmentTime())
                .reason(requestDto.getReason())
                .build();

        // Call service with appointment + IDs
        Appointment created = appointmentService.createNewAppointment(
                appointment,
                requestDto.getDoctorId(),
                requestDto.getPatientId()
        );

        // Convert to DTO before returning
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toAppointmentDto(created));
    }

    // REASSIGN APPOINTMENT TO ANOTHER DOCTOR
    // PUT /hospital/appointments/{appointmentId}/reassign/{doctorId}
    // Example: PUT /hospital/appointments/1/reassign/3
    // Changes the doctor for an existing appointment
    @PutMapping("/appointments/{appointmentId}/reassign/{doctorId}")
    public ResponseEntity<AppointmentResponseDto> reassignDoctor(
            @PathVariable Long appointmentId,
            @PathVariable Long doctorId) {

        Appointment updated = appointmentService
                .reAssignAppointmentToAnotherDoctor(appointmentId, doctorId);

        return ResponseEntity.ok(toAppointmentDto(updated));
    }

    // ── INSURANCE ──────────────────────────────────────────────────────

    // ASSIGN INSURANCE TO PATIENT
    // POST /hospital/patients/{patientId}/insurance
    // Body: {
    //   "policyNumber": "INS-001",
    //   "provider": "LIC",
    //   "validUntil": "2027-12-31"
    // }
    // Returns: Updated patient with insurance assigned
    @PostMapping("/patients/{patientId}/insurance")
    public ResponseEntity<Patient> assignInsurance(
            @PathVariable Long patientId,
            @RequestBody Insurance insurance) {

        Patient updated = insuranceService
                .assignInsuranceToPatient(insurance, patientId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(updated);
    }

    // REMOVE INSURANCE FROM PATIENT
    // DELETE /hospital/patients/{patientId}/insurance
    // orphanRemoval=true → insurance record also deleted from DB
    @DeleteMapping("/patients/{patientId}/insurance")
    public ResponseEntity<String> removeInsurance(
            @PathVariable Long patientId) {

        insuranceService.disassociateInsuranceFromPatient(patientId);

        return ResponseEntity.ok(
                "Insurance removed from patient with id: " + patientId);
    }

    // ── HELPER — Convert Appointment Entity → AppointmentResponseDto ──
    // Private helper — only used inside this controller
    // Prevents circular reference:
    // Appointment → Patient → Appointments → ... (infinite loop)
    private AppointmentResponseDto toAppointmentDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .appointmentTime(appointment.getAppointmentTime())
                .reason(appointment.getReason())

                // Patient summary — just id, name, email
                .patient(appointment.getPatient() != null ?
                        AppointmentResponseDto.PatientSummary.builder()
                                .id(appointment.getPatient().getId())
                                .name(appointment.getPatient().getName())
                                .email(appointment.getPatient().getEmail())
                                .build()
                        : null)

                // Doctor summary — just id, name, specialization
                .doctor(appointment.getDoctor() != null ?
                        AppointmentResponseDto.DoctorSummary.builder()
                                .id(appointment.getDoctor().getId())
                                .name(appointment.getDoctor().getName())
                                .specialization(appointment.getDoctor().getSpecialization())
                                .build()
                        : null)
                .build();
    }
}