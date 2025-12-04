// src/main/java/com/pfe/dataops/dataopsapi/catalog/dto/BulkImportDtos.java
package com.pfe.dataops.dataopsapi.catalog.dto;

import java.util.ArrayList;
import java.util.List;

public class BulkImportDtos {

    public static class FieldDto {
        public String name;
        public String type;
        public String description;
    }

    public static class BulkDatasetRowDto {
        public String name;
        public String domain;
        public String description;
        public String owner_name;
        public String owner_email;
        public List<String> tags;
        public List<FieldDto> fields;
    }

    public static class BulkImportPayloadDto {
        public List<BulkDatasetRowDto> rows;

        public static class SourceDto {
            public String filename;
            public Integer rows;
        }

        public SourceDto source;
    }

    // 🔴 structure alignée avec BulkImportResult (TS)
    public static class ErrorItemDto {
        public int row;
        public String message;

        public ErrorItemDto() {}

        public ErrorItemDto(int row, String message) {
            this.row = row;
            this.message = message;
        }
    }

    public static class BulkImportResultDto {
        public boolean ok;
        public int created;
        public int failed;
        public List<ErrorItemDto> errors = new ArrayList<>();
        public String txId;

        public void addError(int row, String message) {
            this.failed++;
            this.ok = false;
            this.errors.add(new ErrorItemDto(row, message));
        }
    }
}
