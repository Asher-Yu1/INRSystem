package com.inrsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inrsystem.dao.Company;
import com.inrsystem.mapper.CompanyMapper;
import com.inrsystem.service.CompanyService;
import org.springframework.stereotype.Component;

@Component
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
}
