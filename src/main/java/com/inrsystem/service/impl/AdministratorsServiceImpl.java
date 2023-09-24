package com.inrsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inrsystem.dao.Administrators;
import com.inrsystem.dao.Company;
import com.inrsystem.mapper.AdministratorsMapper;
import com.inrsystem.mapper.CompanyMapper;
import com.inrsystem.service.AchievementService;
import com.inrsystem.service.AdministratorsService;
import com.inrsystem.service.CompanyService;
import org.springframework.stereotype.Service;

@Service
public class AdministratorsServiceImpl extends ServiceImpl<AdministratorsMapper, Administrators> implements AdministratorsService {

}
