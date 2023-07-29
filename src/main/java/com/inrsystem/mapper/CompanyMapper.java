package com.inrsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inrsystem.dao.company;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface CompanyMapper extends BaseMapper<company> {
}
