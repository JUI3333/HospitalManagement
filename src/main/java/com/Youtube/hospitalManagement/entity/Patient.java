package com.Youtube.hospitalManagement.entity;

import com.Youtube.hospitalManagement.entity.type.BloodGroupType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToOne(cascade = CascadeType.MERGE, orphanRemoval = true)
    @JoinColumn(name="patient_insurance_id") // Owning Side
    private Insurance insurance;

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    @ToString.Exclude
    private List<Appointment> appointments = new ArrayList<>();
}
