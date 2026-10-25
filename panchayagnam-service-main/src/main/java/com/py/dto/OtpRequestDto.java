package com.py.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OtpRequestDto {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String phoneNumber;

    @NotBlank(message = "Code Request For is required")
    private String codeReqFor;

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the codeReqFor
     */
    public String getCodeReqFor() {
        return codeReqFor;
    }

    /**
     * @param codeReqFor the codeReqFor to set
     */
    public void setCodeReqFor(String codeReqFor) {
        this.codeReqFor = codeReqFor;
    }

}

