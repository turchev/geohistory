package com.company.geohistory.service;

import com.company.geohistory.entity.geo.GeoDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service(GeolocationService.NAME)
public class GeolocationServiceImpl implements GeolocationService {

    private final String BASE_URI = "https://nominatim.openstreetmap.org/search";

    @Override
    public Point findLocationByAddress(String house, String street, String city) {
        String queryUrl = String.format("%s/?city=%s&street=%s %s&format=json",
                BASE_URI,
                Objects.requireNonNullElse(city, ""),
                Objects.requireNonNullElse(house, ""),
                Objects.requireNonNullElse(street, ""));

        RestTemplate restTemplate = new RestTemplate();
        Coordinate coordinate = new Coordinate();
        GeometryFactory geometryFactory = new GeometryFactory();

        ResponseEntity<GeoDto[]> response = restTemplate.getForEntity(queryUrl, GeoDto[].class);
        GeoDto[] geos = response.getBody();
        if (geos.length == 0) {
            return null;
        }

        coordinate.setX(Double.parseDouble(geos[0].getLon()));
        coordinate.setY(Double.parseDouble(geos[0].getLat()));
        return geometryFactory.createPoint(coordinate);
    }

}