package com.inrsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.Team;
import com.inrsystem.mapper.CompanyMapper;
import com.inrsystem.mapper.TeamMapper;
import com.inrsystem.service.CompanyService;
import com.inrsystem.service.TeamService;
import org.springframework.stereotype.Service;

@Service
public class TeamServiceImpl  extends ServiceImpl<TeamMapper, Team> implements TeamService {
}
