package com.duru100470.study.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ExceptionEnum {
    // Api Exception
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST),
    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN),
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

    DUPLICATE_DATA_EXCEPTION(HttpStatus.CONFLICT);

    private final HttpStatus status;
    private String message;

    ExceptionEnum(HttpStatus status) {
        this.status = status;
    }

    ExceptionEnum(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
