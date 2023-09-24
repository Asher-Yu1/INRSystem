package com.inrsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.Event;
import com.inrsystem.mapper.CompanyMapper;
import com.inrsystem.mapper.EventMapper;
import com.inrsystem.service.CompanyService;
import com.inrsystem.service.EventService;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl  extends ServiceImpl<EventMapper, Event> implements EventService {
}
