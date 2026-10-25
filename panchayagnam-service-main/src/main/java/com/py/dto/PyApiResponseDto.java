package com.py.dto;

public class PyApiResponseDto<T> {

    private boolean success;
    private String message;
    private T data;

    public PyApiResponseDto() {
    }

    public PyApiResponseDto(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    // Success response
    public static <T> PyApiResponseDto<T> success(String message, T data) {
        return new PyApiResponseDto<>(true, message, data);
    }

    // Failure response
    public static <T> PyApiResponseDto<T> failure(String message) {
        return new PyApiResponseDto<>(false, message, null);
    }
}
