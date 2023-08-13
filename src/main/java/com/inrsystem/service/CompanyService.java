package com.inrsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inrsystem.dao.Company;
import org.springframework.stereotype.Component;

@Component
public interface CompanyService extends IService<Company> {
    Company getCompany(String account);
}
