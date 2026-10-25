// PyResetMpinRequestDto.java
package com.py.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PyResetMpinRequestDto {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp;

    @NotBlank(message = "New MPIN is required")
    @Pattern(regexp = "\\d{4}", message = "MPIN must be exactly 4 digits")
    private String newMpin;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewMpin() {
        return newMpin;
    }

    public void setNewMpin(String newMpin) {
        this.newMpin = newMpin;
    }
}
