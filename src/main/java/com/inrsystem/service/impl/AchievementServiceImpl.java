package com.inrsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inrsystem.dao.Achievement;
import com.inrsystem.dao.Company;
import com.inrsystem.mapper.AchievementMapper;
import com.inrsystem.mapper.CompanyMapper;
import com.inrsystem.service.AchievementService;
import com.inrsystem.service.CompanyService;
import org.springframework.stereotype.Service;

@Service
public class AchievementServiceImpl  extends ServiceImpl<AchievementMapper, Achievement> implements AchievementService {
}
