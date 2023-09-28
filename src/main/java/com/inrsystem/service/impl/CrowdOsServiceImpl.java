package com.inrsystem.service.impl;

import cn.crowdos.kernel.resource.Participant;
import cn.crowdos.kernel.resource.Task;
import com.inrsystem.dao.*;
import com.inrsystem.service.CrowdOsService;

import java.util.ArrayList;
import java.util.List;

public class CrowdOsServiceImpl implements CrowdOsService {
    final CrowdKernelComponent crowdKernelComponent;

    public CrowdOsServiceImpl(CrowdKernelComponent crowdKernelComponent) {
        this.crowdKernelComponent = crowdKernelComponent;
    }

    @Override
    public void registerParticipant(Company company) {
        crowdKernelComponent.getKernel().registerParticipant(company);
    }
    @Override
    public void registerParticipant(Administrators administrators) {
        crowdKernelComponent.getKernel().registerParticipant(administrators);
    }
    @Override
    public void registerParticipant(TeamMembers teamMembers) {
        crowdKernelComponent.getKernel().registerParticipant(teamMembers);
    }
    @Override
    public List<TeamMembers> getTaskRecommendation(Event event) {
        ArrayList<TeamMembers> users = new ArrayList<>();
        List<Participant> taskRecommendationScheme = crowdKernelComponent.getKernel().getTaskRecommendationScheme((Task) event);
        for (Participant participant : taskRecommendationScheme) {
            users.add((TeamMembers) participant);
        }
        return users;
    }
}
