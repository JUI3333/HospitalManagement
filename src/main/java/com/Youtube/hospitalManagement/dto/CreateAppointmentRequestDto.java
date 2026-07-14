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
public class CreateAppointmentRequestDto {

    // When the appointment is scheduled
    private LocalDateTime appointmentTime;

    // Why the patient is coming
    private String reason;

    // Which doctor to book with
    // Controller will pass these to AppointmentService
    private Long doctorId;

    // Which patient is booking
    private Long patientId;
}