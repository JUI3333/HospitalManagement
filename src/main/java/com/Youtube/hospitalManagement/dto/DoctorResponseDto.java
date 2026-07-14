package com.Youtube.hospitalManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponseDto {

    private Long id;
    private String name;
    private String specialization;
    private String email;

    // Department names only — not full Department objects
    private List<String> departmentNames;

    // Appointment summaries for this doctor
    private List<AppointmentSummary> appointments;

    // ── Nested summary class ──────────────────────────────────────
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppointmentSummary {
        private Long id;
        private LocalDateTime appointmentTime;
        private String reason;
        private String patientName; // just name, no full Patient object
    }
}