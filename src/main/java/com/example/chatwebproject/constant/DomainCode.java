package com.example.chatwebproject.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DomainCode {
    SUCCESS("TD-000", "Success", HttpStatus.OK.value()),
    INVALID_PARAMETER("TD-001", "Invalid parameter: %s", HttpStatus.BAD_REQUEST.value()),
    INTERNAL_SERVICE_ERROR("TD-002", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    FORBIDDEN("TD-003", "Forbidden", HttpStatus.FORBIDDEN.value()),
    NO_PERMISSION("TD-004", "No permission", HttpStatus.FORBIDDEN.value()),
    DEPLOY_CAMUNDA_FAIL("TD-005", "Deploy camunda fail", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    CONFLICT_ERROR("TD-006", "The resource was modified by another transaction", HttpStatus.CONFLICT.value()),
    EXPECTATION_FAILED("TD-007", "EXPECTATION_FAILED", HttpStatus.EXPECTATION_FAILED.value()),
//    DATA_NOT_FOUND("TD-008", "NO_DATA_FOUND, %s", HttpStatus.BAD_REQUEST.value()),
    IMPORT_DATA_ERROR("TD-009", "IMPORT_DATA_ERROR, %s", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    DOWNLOAD_FILE_FAIL("TD-010", "File download fail", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    MINIO_UPLOAD_FAIL("TD-011", "Upload file to minIO fail", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    MINIO_GEN_LINK_FAIL("TD-012", "Gen link file minIO fail", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    FILE_NOT_ACCEPT("TD-013", "File size has less than 16MB", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    FILE_NOT_SUPPORT("TD-014", "File type not support", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    FILE_CAMUNDA_NOT_ALLOW("TD-015", "Camunda file not allow", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    NOT_FOUND_DATA("TD-016", "Not found data", HttpStatus.NOT_FOUND.value()),
    CAMUNDA_INFO_PROCESS_FAIL("TD-017", "Get camunda process info fail", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    CAMUNDA_START_PROCESS_FAIL("TD-018", "Start process camunda fail", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    CAMUNDA_SUBMIT_PROCESS_FAIL("TD-019", "Submit process fail", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    CAMUNDA_INFO_FORBIDDEN("TD-020", "Get camunda rule info fail", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    DUPLICATE_EMAIL_CONFIG_CODE("TD-021", "Duplicate email config code", HttpStatus.OK.value()),
    ;

    private final String code;
    private final String message;
    private final int status;

    DomainCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}

