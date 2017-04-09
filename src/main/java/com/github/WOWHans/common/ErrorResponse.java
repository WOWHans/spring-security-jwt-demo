package com.github.WOWHans.common;

import org.springframework.http.HttpStatus;

import java.util.Date;

/**
 * Created by Hans on 2017/4/9.
 */
public class ErrorResponse {

    private HttpStatus status;
    private String message;
    private Date timestamp;

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
    public static ErrorResponse out(String message, HttpStatus status){
        return new ErrorResponse(status,message);
    }

    public Integer getStatus() {
        return status.value();
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
