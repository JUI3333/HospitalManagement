package com.Youtube.hospitalManagement.service;

import com.Youtube.hospitalManagement.entity.Appointment;
import com.Youtube.hospitalManagement.entity.Doctor;
import com.Youtube.hospitalManagement.entity.Patient;
import com.Youtube.hospitalManagement.repository.AppointmentRepo;
import com.Youtube.hospitalManagement.repository.DoctorRepo;
import com.Youtube.hospitalManagement.repository.PatientRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepo appointmentRepo;
    private final DoctorRepo doctorRepo;
    private final PatientRepo patientRepo;

    @Transactional
    public Appointment createNewAppointment(
            Appointment appointment,
            Long doctorId,
            Long patientId) {

        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow();
        Patient patient = patientRepo.findById(patientId).orElseThrow();

        if (appointment.getId() != null) {
            throw new IllegalArgumentException(
                    "Appointment should not have an ID");
        }

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        patient.getAppointments().add(appointment);
        doctor.getAppointments().add(appointment);

        return appointmentRepo.save(appointment);
    }

    @Transactional
    public Appointment reAssignAppointmentToAnotherDoctor(Long appointmentId, Long doctorId)
    {
        Appointment appointment = appointmentRepo.findById(appointmentId).orElseThrow();
        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow();
        appointment.setDoctor(doctor);

        // maintain bidirectional relationship
        doctor.getAppointments().add(appointment);

        return appointment;
    }
}