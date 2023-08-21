package com.inrsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface TeamMapper extends BaseMapper<Team> {
    @Select("SELECT COUNT(`name`) FROM `team` WHERE  name=\"${name}\"")
    Integer getSameName(@Param("name")String name);

}
