package com.py.easytrade.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request payload for EasyTrade user registration/login")
public class EasyTradeRegisterRequestPojo {

    @NotBlank
    @Schema(description = "First name", example = "John")
    private String firstName;

    @NotBlank
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Email
    @Schema(description = "Email address", example = "john.doe@email.com")
    private String email;

    @NotBlank
    @Schema(description = "Password", example = "SecurePass@123")
    private String password;

    @NotBlank
    @Pattern(regexp = "\\+\\d{1,4}", message = "Invalid country code")
    @Schema(description = "Country code with + prefix", example = "+91")
    private String countryCode;   // +91

    @NotBlank
    @Pattern(regexp = "\\d{8,15}", message = "Invalid mobile number")
    @Schema(description = "Mobile number (8-15 digits)", example = "9876543210")
    private String mobileNumber;  // 10â€“15 digits

    // getters & setters

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * @return the mobileNumber
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * @param mobileNumber the mobileNumber to set
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
