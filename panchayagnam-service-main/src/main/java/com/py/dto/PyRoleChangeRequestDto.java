package com.py.dto;

import jakarta.validation.constraints.NotNull;

public class PyRoleChangeRequestDto {

    @NotNull(message = "superAdminPyId is required")
    private Long superAdminPyId;

    @NotNull(message = "targetPyId is required")
    private Long targetPyId;

    public Long getSuperAdminPyId() {
        return superAdminPyId;
    }

    public void setSuperAdminPyId(Long superAdminPyId) {
        this.superAdminPyId = superAdminPyId;
    }

    public Long getTargetPyId() {
        return targetPyId;
    }

    public void setTargetPyId(Long targetPyId) {
        this.targetPyId = targetPyId;
    }
}
