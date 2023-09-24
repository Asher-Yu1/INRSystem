package com.inrsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inrsystem.dao.Company;
import com.inrsystem.mapper.CompanyMapper;
import com.inrsystem.service.CompanyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
@Resource
private CompanyMapper companyMapper;
    @Override
    public Company getCompany(String account){
        Map<String,Object> map=new HashMap<>();
        map.put("account",account);
        Company company = companyMapper.selectByMap(map).get(0);
        return company;
    }
}
