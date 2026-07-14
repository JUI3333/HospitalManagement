package com.Youtube.hospitalManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDto {

    private Long id;
    private LocalDateTime appointmentTime;
    private String reason;

    // Patient summary — just key info
    private PatientSummary patient;

    // Doctor summary — just key info
    private DoctorSummary doctor;

    // ── Nested summary classes ─────────────────────────────────────
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatientSummary {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DoctorSummary {
        private Long id;
        private String name;
        private String specialization;
    }
}