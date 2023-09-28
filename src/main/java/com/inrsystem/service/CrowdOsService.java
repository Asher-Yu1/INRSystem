package com.inrsystem.service;

import com.inrsystem.dao.Administrators;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.Event;
import com.inrsystem.dao.TeamMembers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CrowdOsService {
    void registerParticipant(Company company);

    void registerParticipant(Administrators administrators);

    void registerParticipant(TeamMembers teamMembers);

    List<TeamMembers> getTaskRecommendation(Event event);


}
