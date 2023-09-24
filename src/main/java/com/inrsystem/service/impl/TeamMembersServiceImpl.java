package com.inrsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.TeamMembers;
import com.inrsystem.mapper.CompanyMapper;
import com.inrsystem.mapper.TeamMembersMapper;
import com.inrsystem.service.CompanyService;
import com.inrsystem.service.TeamMembersService;
import org.springframework.stereotype.Service;

@Service
public class TeamMembersServiceImpl  extends ServiceImpl<TeamMembersMapper, TeamMembers> implements TeamMembersService {
}
