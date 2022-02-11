package com.imjustdoom.pluginsite.config.exception;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class RestException extends Exception {
    private final RestErrorCode errorCode;
    private final @Nullable String message;

    public RestException(RestErrorCode errorCode, String message, Object... params) {
        this.errorCode = errorCode;
        this.message = String.format(message, params);
    }

    public RestException(RestErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = null;
    }
}
