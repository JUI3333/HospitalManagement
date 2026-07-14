package com.Youtube.hospitalManagement.repository;

import com.Youtube.hospitalManagement.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepo extends JpaRepository<Doctor, Long> {

    // Used in DoctorService.createDoctor() to check email uniqueness
    // Derived query — Spring auto-generates:
    // SELECT * FROM doctor WHERE email = ?
    Optional<Doctor> findByEmail(String email);

    // Used in DoctorService.getDoctorsBySpecialization()
    // Derived query — Spring auto-generates:
    // SELECT * FROM doctor WHERE specialization = ?
    List<Doctor> findBySpecialization(String specialization);

    // Fetch doctors with their appointments in one query
    // Solves N+1 problem same as PatientRepo.findAllPatientWithAppointments()
    @Query("SELECT d FROM Doctor d LEFT JOIN FETCH d.appointments")
    List<Doctor> findAllDoctorsWithAppointments();
}