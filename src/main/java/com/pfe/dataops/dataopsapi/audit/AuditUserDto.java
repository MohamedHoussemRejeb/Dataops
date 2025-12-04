// src/main/java/com/pfe/dataops/dataopsapi/audit/AuditUserDto.java
package com.pfe.dataops.dataopsapi.audit;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditUserDto {
    private String name;
    private String email;
}
