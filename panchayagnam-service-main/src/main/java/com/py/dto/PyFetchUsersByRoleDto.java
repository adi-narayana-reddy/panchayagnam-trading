package com.py.dto;

import jakarta.validation.constraints.NotBlank;

public class PyFetchUsersByRoleDto {

    @NotBlank(message = "Role is required")
    private String role; // SUPER_ADMIN, ADMIN, USER
    

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
