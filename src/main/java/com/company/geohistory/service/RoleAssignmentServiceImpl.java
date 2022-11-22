package com.company.geohistory.service;

import io.jmix.core.UnconstrainedDataManager;
import io.jmix.core.querycondition.LogicalCondition;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.securitydata.entity.RoleAssignmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(RoleAssignmentService.NAME)
public class RoleAssignmentServiceImpl implements RoleAssignmentService {

    @Autowired
    private UnconstrainedDataManager unconstrainedDataManager;

    @Override
    public void createRoleAssignmentByCode(String username, String roleCode) {
        RoleAssignmentEntity roleAssignment = unconstrainedDataManager.create(RoleAssignmentEntity.class);
        roleAssignment.setUsername(username);
        roleAssignment.setRoleCode(roleCode);
        roleAssignment.setRoleType("resource");
        unconstrainedDataManager.save(roleAssignment);
    }

    public void deleteRoleAssignmentByCode(String username, String roleCode) {
        unconstrainedDataManager.load(RoleAssignmentEntity.class)
                .condition(
                        LogicalCondition.and(
                                PropertyCondition.contains("roleCode", roleCode),
                                PropertyCondition.contains("username", username)
                        )
                )
                .optional().ifPresent(
                        roleAssignment -> unconstrainedDataManager.remove(roleAssignment)
                );
    }

}