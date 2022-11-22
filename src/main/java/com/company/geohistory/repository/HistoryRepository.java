package com.company.geohistory.repository;

import com.company.geohistory.entity.geo.History;
import io.jmix.core.repository.JmixDataRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HistoryRepository extends JmixDataRepository<History, UUID> {
}
