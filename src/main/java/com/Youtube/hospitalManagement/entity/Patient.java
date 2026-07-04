package com.Youtube.hospitalManagement.entity;

import com.Youtube.hospitalManagement.entity.type.BloodGroupType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
@Table(
        uniqueConstraints = {
                //@UniqueConstraint(name="unique_patient_email", columnNames = {"email"}),
                @UniqueConstraint(name="unique_patient_name_birthdate", columnNames = {"patient_name","birth_date"}),
        },
        indexes = {
                @Index(name="idx_patient_birth_date", columnList = "birth_date")
        }
)
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_name", nullable= false, length=40)
    private String name;

    @ToString.Exclude
    private LocalDate birthDate;

    @Column(unique = true)
    private String email;
    private String gender;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private BloodGroupType bloodGroup;
}
