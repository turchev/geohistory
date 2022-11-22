package com.company.geohistory.service;

import org.locationtech.jts.geom.Point;

public interface GeolocationService {
    String NAME = "geo_GeolocationService";

    Point findLocationByAddress(String city, String street, String house);
}