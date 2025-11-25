package com.dhami.hospital.management.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "patient",
        uniqueConstraints = {
//                @UniqueConstraint(name = "unique_patient_email", columnNames = {"email"}),
                @UniqueConstraint(name = "unique_patient_name_birthdate", columnNames = {"name", "birthDate"})
        },
        indexes = {//data retrieval ke time query ko fast krna (create index using algo) so -> o(n) to o(logn)
                @Index(name = "idx_patient_birth_date", columnList = "birthDate")
        }
)
@Getter@Setter
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String name;

    //    @ToString.Exclude
    private LocalDate birthDate;

    @Column(unique = true, nullable = false)
    private String email;

    private String gender;

    //@Enumerated(EnumType.STRING)
    private String bloodGroup;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    //customize fk name
    @JoinColumn(name = "patient_insurance_id") // owning side
    private Insurance insurance;

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Appointment> appointments = new ArrayList<>();
}

/*
✅ What is @Entity?
@Entity tells Spring + Hibernate: -> "This class represents a table in the database."
so : The class = a table && The fields/variables = the columns in that table
why --> Without @Entity, Hibernate will ignore the class completely
note--> ✔️ Every @Entity class MUST have:A primary key → @Id && A no-args constructor (default constructor)

✅ What is @Id?
-> "This field is the PRIMARY KEY of the table."
Just like every database table needs a unique identifier,
your Entity class also needs one.

🚀 With auto-generated ID
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
-> "Database automatically generates the ID (1, 2, 3, 4…)"


 */
