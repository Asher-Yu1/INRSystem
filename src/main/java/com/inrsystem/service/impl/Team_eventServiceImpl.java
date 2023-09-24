package com.inrsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inrsystem.dao.Team_event;
import com.inrsystem.mapper.Team_eventMapper;
import com.inrsystem.service.Team_eventService;
import org.springframework.stereotype.Service;

@Service
public class Team_eventServiceImpl extends ServiceImpl<Team_eventMapper,Team_event>  implements Team_eventService {

}
