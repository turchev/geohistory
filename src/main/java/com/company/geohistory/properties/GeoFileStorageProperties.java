package com.company.geohistory.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "geo")
public class GeoFileStorageProperties {

    private final String DEFAULT_SYSTEM_FILE_DIR = "${user.dir}/fs/system";
    private final String DEFAULT_PROFILE_FILE_DIR = "${user.dir}/fs/profile";

    private String systemFileDir;
    private String profileFileDir;

    public String getSystemFileDir() {
        return systemFileDir == null ? DEFAULT_SYSTEM_FILE_DIR : systemFileDir;
    }

    public String getProfileFileDir() {
        return profileFileDir == null ? DEFAULT_PROFILE_FILE_DIR : profileFileDir;
    }


    public void setSystemFileDir(String systemFileDir) {
        this.systemFileDir = systemFileDir;
    }

    public void setProfileFileDir(String profileFileDir) {
        this.profileFileDir = profileFileDir;
    }
}
