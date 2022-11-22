package com.company.geohistory.properties;

import io.jmix.core.FileStorage;
import io.jmix.localfs.LocalFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(GeoFileStorageProperties.class)

public class GeoFileStorageConfiguration {

    @Autowired
    GeoFileStorageProperties geoFileStorageProperties;

    @Bean
    FileStorage profileFileStorage() {
        return new LocalFileStorage("profileFileFs", geoFileStorageProperties.getProfileFileDir());
    }

    @Bean
    @Primary
    FileStorage systemFileStorage() {
        return new LocalFileStorage("systemFileFs", geoFileStorageProperties.getSystemFileDir());
    }

}
