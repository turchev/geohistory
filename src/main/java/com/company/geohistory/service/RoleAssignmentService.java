package com.company.geohistory.service;

import javax.validation.constraints.NotNull;

public interface RoleAssignmentService {
    String NAME = "geo_RoleAssignmentService";

    void createRoleAssignmentByCode(@NotNull String username, @NotNull String roleCode);

    void deleteRoleAssignmentByCode(@NotNull String username, @NotNull String roleCode);
}
