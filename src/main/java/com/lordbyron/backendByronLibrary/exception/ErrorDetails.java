package com.lordbyron.backendByronLibrary.exception;

import java.util.Date;

public class ErrorDetails {
    private Date date;
    private String message;
    private String exception;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public ErrorDetails() {
    }

    public ErrorDetails(Date date, String message, String exception) {
        this.date = date;
        this.message = message;
        this.exception = exception;
    }
}
