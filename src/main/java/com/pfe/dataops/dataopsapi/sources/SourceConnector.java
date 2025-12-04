// src/main/java/com/pfe/dataops/dataopsapi/sources/SourceConnector.java
package com.pfe.dataops.dataopsapi.sources;

public interface SourceConnector {
    boolean supports(SoftwareSource source);   // type == 'api' / 'db' / 'ftp' / 'file'
    HealthCheckResult test(SoftwareSource source) throws Exception;
}
