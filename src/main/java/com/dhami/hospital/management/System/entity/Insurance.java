package com.dhami.hospital.management.System.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String policyNumber;

    @Column(nullable = false, length = 100)
    private String provider;

    @Column(nullable = false)
    private LocalDate validUntil;

    @CreationTimestamp//from hibernate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //OneToOne means define association
   // without mapped by this create auto insurance_id in this table (ambiguity) so arrise concept
    //owing side and inverse side
    //single source of truth
    //hme bidirectional bhi krni h or ek hi side column then mappedBy value
    @OneToOne(mappedBy = "insurance") // inverse side
    private Patient patient;
}
