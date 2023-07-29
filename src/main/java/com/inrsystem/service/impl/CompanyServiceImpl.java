package com.inrsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inrsystem.dao.company;
import com.inrsystem.mapper.CompanyMapper;
import com.inrsystem.service.CompanyService;
import org.springframework.stereotype.Component;

@Component
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, company> implements CompanyService {
}
