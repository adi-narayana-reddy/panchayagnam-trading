package com.py.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PyLoginRequestDto {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String phoneNumber;

    @NotBlank(message = "MPIN is required")
    @Pattern(regexp = "^\\d{4}$", message = "MPIN must be exactly 4 digits")
    // @Pattern(regexp = "^\\d{4,6}$", message = "MPIN must be 4 or 6 digits") // Future: 4 or 6 digits
    private String mpin;

    public PyLoginRequestDto() {}

    public PyLoginRequestDto(String phoneNumber, String mpin) {
        this.phoneNumber = phoneNumber;
        this.mpin = mpin;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }
}