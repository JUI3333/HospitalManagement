package com.Youtube.hospitalManagement;

import com.Youtube.hospitalManagement.entity.Appointment;
import com.Youtube.hospitalManagement.entity.Insurance;
import com.Youtube.hospitalManagement.entity.Patient;
import com.Youtube.hospitalManagement.repository.PatientRepo;
import com.Youtube.hospitalManagement.service.AppointmentService;
import com.Youtube.hospitalManagement.service.InsuranceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
public class InsuranceTests {

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private AppointmentService appointmentService;

    @Test
    public void testInsurance() {

        Insurance insurance = Insurance.builder()
                .policyNumber("HDFC_1234")
                .provider("HDFC")
                .validUntil(LocalDate.of(2030, 12, 12))
                .build();

//        Patient patient = Patient.builder()
//                .name("Jui")
//                .email("jui@gmail.com")
//                .gender("Female")
//                .build();
        // save patient first
        //patient = patientRepo.save(patient);

        // assign insurance
        Patient patient = insuranceService.assignInsuranceToPatient(insurance,1L);
        System.out.println(patient);

        var updatedPatient = insuranceService.disassociateInsuranceFromPatient(patient.getId());
        System.out.println(updatedPatient);
    }

    @Test
    public void testCreateAppointment() {

        Appointment appointment = Appointment.builder()
                .appointmentTime(LocalDateTime.of(2025, 11, 1, 14, 30))
                .reason("Cancer")
                .build();

        var newAppointment = appointmentService.createNewAppointment(appointment, 1L,1L);
        System.out.println(newAppointment);

        var updatedAppointment = appointmentService.reAssignAppointmentToAnotherDoctor(newAppointment.getId(),3L);
        System.out.println(updatedAppointment);
    }
}