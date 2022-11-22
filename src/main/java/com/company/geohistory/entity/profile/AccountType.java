package com.company.geohistory.entity.profile;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum AccountType implements EnumClass<String> {

    ADMIN("system-full-access"),
    USER("user-restricted-access");

    private String id;

    AccountType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static AccountType fromId(String id) {
        for (AccountType at : AccountType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

}