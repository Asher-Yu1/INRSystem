package com.inrsystem.service;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;


public interface CrowdOsAlgorithmService {
    void getAllowedTeam() throws MessagingException;
}
