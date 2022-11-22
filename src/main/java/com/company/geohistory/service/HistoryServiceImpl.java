package com.company.geohistory.service;

import com.company.geohistory.entity.User;
import com.company.geohistory.entity.geo.History;
import com.company.geohistory.repository.HistoryRepository;
import io.jmix.core.security.CurrentAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service(HistoryService.NAME)
public class HistoryServiceImpl implements HistoryService{
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Override
    public History saveWithUserAndTime(History history) {
        history.setDateTime(LocalDateTime.now());
        history.setUser((User) currentAuthentication.getUser());
        return historyRepository.save(history);
    }

}