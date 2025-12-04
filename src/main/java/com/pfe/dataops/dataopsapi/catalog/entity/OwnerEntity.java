package com.pfe.dataops.dataopsapi.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "owners")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OwnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length = 120)
    private String name;

    @Column(nullable=false, length = 180)
    private String email;
}
