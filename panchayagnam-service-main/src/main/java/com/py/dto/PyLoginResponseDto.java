package com.py.dto;

public class PyLoginResponseDto {

    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private String token;
    private Long expiresInDays;
    private Boolean expiredFlag;
    private Integer noOfClients;

    public Long getExpiresInDays() {
        return expiresInDays;
    }

    public void setExpiresInDays(Long expiresInDays) {
        this.expiresInDays = expiresInDays;
    }

    public Boolean getExpiredFlag() {
        return expiredFlag;
    }

    public void setExpiredFlag(Boolean expiredFlag) {
        this.expiredFlag = expiredFlag;
    }

    public PyLoginResponseDto() {
    }

    public PyLoginResponseDto(Long userId, String fullName, String email,
            String phoneNumber, String role, String token) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public void setNoOfClients(Integer noOfClients) {
        this.noOfClients = noOfClients;
    }
    public Integer getNoOfClients() {
        return noOfClients;
    }
}
