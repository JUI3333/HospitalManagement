package com.Youtube.hospitalManagement.dto;

import com.Youtube.hospitalManagement.entity.type.BloodGroupType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponseDto {

    // Basic patient info
    private Long id;
    private String name;
    private String email;
    private String gender;
    private LocalDate birthDate;
    private BloodGroupType bloodGroup;

    // When was this patient registered
    private LocalDateTime createdAt;

    // Insurance summary — just key fields, not the full entity
    // We never expose full nested objects in response DTOs
    private InsuranceSummary insurance;

    // List of appointment summaries for this patient
    private List<AppointmentSummary> appointments;

    // ── Nested summary classes ─────────────────────────────────────
    // These are inner classes — they live inside PatientResponseDto
    // They give just enough info without circular references
    // (Patient → Appointment → Patient → ... would cause infinite loop)

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InsuranceSummary {
        private Long id;
        private String policyNumber;
        private String provider;
        private LocalDate validUntil;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppointmentSummary {
        private Long id;
        private LocalDateTime appointmentTime;
        private String reason;
        private String doctorName; // just the name, not full Doctor object
    }
}