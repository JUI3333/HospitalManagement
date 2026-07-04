package com.Youtube.hospitalManagement;

import java.time.LocalDate;
import java.util.List;

import com.Youtube.hospitalManagement.dto.BloodGroupCountResponseEntity;
import com.Youtube.hospitalManagement.entity.Patient;
import com.Youtube.hospitalManagement.repository.PatientRepo;
import com.Youtube.hospitalManagement.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
public class PatientTests {

    @Autowired
    private PatientRepo patientRepository;

    @Autowired
    private PatientService patientService;

    @Test
    public void testPatientRepo(){
        List<Patient> patientList = patientRepository.findAll();
        System.out.println(patientList);

        Patient p1 = new Patient();
        p1.setName("Raj");
        p1.setBirthDate(LocalDate.of(2002,5,20));
        p1.setEmail("raj@gmail.com");
        p1.setGender("Male");

        patientRepository.save(p1);
    }

    @Test
    public void testTransactionMethods(){
        //Patient patient = patientService.getPatientById(1L);

        //Patient patient = patientRepository.findById(1L).orElseThrow()

        //Patient patient = patientRepository.findByName("Jui Sonawane");

//        List<Patient> patientList = patientRepository.findByNameContainingOrderByIdDesc("Di");
//        for(Patient patient: patientList) {
//            System.out.println(patient);
//        }

        Page<Patient> patientPage =
                patientRepository.findAllPatients(PageRequest.of(0,2, Sort.by("Name")));

        for (Patient patient : patientPage.getContent()) {
            System.out.println(patient);
        }

//        List<Object[]> bloodGroupList = patientRepository.countEachBloodGroupType();
//        for(Object[] objects: bloodGroupList){
//            System.out.println(objects[0]+" "+objects[1]);
//        }

        int rowUpdated = patientRepository.updateNameWithId("Aarav Wagh",1L);
        System.out.println(rowUpdated);

        List<BloodGroupCountResponseEntity> bloodGroupList = patientRepository.countEachBloodGroupType();
        for(BloodGroupCountResponseEntity bloodGroupCountResponse: bloodGroupList){
            System.out.println(bloodGroupCountResponse);
        }
    }

}
