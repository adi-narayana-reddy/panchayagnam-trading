package com.py.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int statusCode;
    private String status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.statusCode = 200;
        response.status = "SUCCESS";
        response.message = message;
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.statusCode = statusCode;
        response.status = "ERROR";
    response.message = com.py.controller.utils.ExceptionSanitizer.sanitize(message);
        return response;
    }
    

    public int getStatusCode() { return statusCode; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}

