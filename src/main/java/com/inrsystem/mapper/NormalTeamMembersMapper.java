package com.inrsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inrsystem.dao.NormalTeamMembers;
import com.inrsystem.dao.TeamMembers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jsoup.Connection;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface NormalTeamMembersMapper extends BaseMapper<NormalTeamMembers> {

    @Select("SELECT * FROM teamMembers WHERE team_id=#{team_id}")
    List<NormalTeamMembers> getSameTeamMembers(@Param("team_id")Long teamId);
}
