package com.imjustdoom.pluginsite.config.exception;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

public enum RestErrorCode {
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "auth", 1),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "auth", 2),
    USERNAME_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "auth", 3),
    EMAIL_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "auth", 4),

    INVALID_ACCESS(HttpStatus.UNAUTHORIZED, "auth", 100),

    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "data", 100),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "data", 101),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "data", 102),
    RESOURCE_UPDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "data", 103),
    DOWNLOAD_NOT_FOUND(HttpStatus.NOT_FOUND, "data", 104),

    WRONG_FILE_TYPE(HttpStatus.BAD_REQUEST, "data", 2),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "data", 3),
    PAGE_SIZE_TOO_LARGE(HttpStatus.BAD_REQUEST, "data", 4),
    REQUIRED_ARGUMENTS_MISSING(HttpStatus.BAD_REQUEST, "data", 5),

    TOO_MANY_RESOURCE_CREATIONS(HttpStatus.TOO_MANY_REQUESTS, "resource", 1),
    TOO_MANY_RESOURCE_UPDATES(HttpStatus.TOO_MANY_REQUESTS, "resource", 2),
    RESOURCE_NAME_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "resource", 3);

    private final HttpStatus httpStatus;
    private final String module;
    private final int errorCode;

    RestErrorCode(HttpStatus httpStatus, String module, int errorCode) {
        this.httpStatus = httpStatus;
        this.module = module;
        this.errorCode = errorCode;
    }

    @JsonIgnore
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getModule() {
        return this.module;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
