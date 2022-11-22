package com.company.geohistory.security;

import com.company.geohistory.entity.User;
import com.company.geohistory.entity.geo.GeoDto;
import com.company.geohistory.entity.geo.History;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securitydata.entity.RoleAssignmentEntity;

@ResourceRole(name = "UserRole", code = UserRole.CODE)
public interface UserRole {
    String CODE = "user-restricted-access";

    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.READ)
    void user();

    //  TODO: change to current user only
    @EntityAttributePolicy(entityClass = RoleAssignmentEntity.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = RoleAssignmentEntity.class, actions = EntityPolicyAction.ALL)
    void roleAssignmentEntity();

    @EntityAttributePolicy(entityClass = History.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = History.class, actions = EntityPolicyAction.ALL)
    void history();

    @EntityAttributePolicy(entityClass = GeoDto.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = GeoDto.class, actions = EntityPolicyAction.READ)
    void geoDto();
}