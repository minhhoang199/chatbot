package com.example.chatwebproject.constant;

/**
 * Application constants.
 */
public final class Constants {

    public static final String SYSTEM = "system";

    private Constants() {
    }

    public static final String MESSAGE_SOURCE_BASE_NAMES = "classpath:messages";
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String VI_LANG = "vi";
    public static final String EN_LANG = "en";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";

    // Directory to save uploaded files
    public static final String UPLOADED_FOLDER = "uploads/";

    public static final String BPMN = "bpmn";
    public static final String DMN = "dmn";
    public static final String CAMUNDA_SUCCESS_STATUS = "CD-000";
    public static final String STATUS = "status";
    public static final String SLA_FORMAT = "%.2f";
    public static final String YYYY_MM_DD_FORMAT = "yyyyMMdd";

    public static final String SLASH = "/";
    public static final String DASH = "_";
    public static final String YES = "Có";
    public static final String NO = "Không";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String COLON = ":";
    public static final String SPACE = " ";
    public static final String EMPTY_STRING = "";

    // Đơn vị kinh doanh
    public static final class BuSubmitType {
        private BuSubmitType() {
        }
        public static final String SUBMIT_TRANSFER_REQUEST = "BU_SUBMIT_TRANSFER_REQUEST"; // Trình hồ sơ
        public static final String CANCEL_TRANSFER_REQUEST = "BU_CANCEL_TRANSFER_REQUEST"; // Hủy hồ sơ
        public static final String DELETE_TRANSFER_REQUEST = "BU_DELETE_TRANSFER_REQUEST"; // Xóa hồ sơ
    }

    // Chuyên viên quản lý giao dịch tại quầy
    public static final class VerifierSubmitType {
        private VerifierSubmitType() {
        }
        public static final String REJECT = "VERIFIER_REJECT"; // CV từ chối hồ sơ
        public static final String APPROVE = "VERIFIER_APPROVE"; // CV Phê duyệt hồ sơ
    }
}

