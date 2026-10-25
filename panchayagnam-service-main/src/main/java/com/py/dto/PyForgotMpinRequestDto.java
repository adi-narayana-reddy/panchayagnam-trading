// PyForgotMpinRequestDto.java
package com.py.dto;

import jakarta.validation.constraints.NotBlank;

public class PyForgotMpinRequestDto {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
