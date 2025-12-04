package com.pfe.dataops.dataopsapi.catalog.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OwnerDto {
    private Long id;
    private String name;
    private String email;
}
