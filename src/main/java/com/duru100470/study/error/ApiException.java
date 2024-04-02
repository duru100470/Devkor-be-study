package com.duru100470.study.error;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private ExceptionEnum error;
    private String message;

    public ApiException(ExceptionEnum e) {
        super(e.getMessage());
        this.error = e;
    }

    public ApiException(ExceptionEnum e, String message) {
        this.error = e;
        this.message = message;
    }
}
