package com.company.geohistory.service;

import com.company.geohistory.entity.geo.History;

public interface HistoryService {
    String NAME = "geo_HistoryService";

    History saveWithUserAndTime(History history);
}
