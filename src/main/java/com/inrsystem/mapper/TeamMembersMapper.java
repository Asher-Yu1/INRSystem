package com.inrsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.TeamMembers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface TeamMembersMapper extends BaseMapper<TeamMembers> {
    @Update("UPDATE research_teamMembers SET team_id=#{team_id} ,team_role =#{teamRole} WHERE name=\"${name}\"  AND id=#{id}")
    Boolean creatTeam(@Param("team_id")Integer teamId,@Param("teamRole") Integer teamRole, @Param("id") Integer id,@Param("name")String name);

    @Select("SELECT * FROM research_teamMembers WHERE team_id=#{team_id}")
    List<TeamMembers> getSameTeamMembers(@Param("team_id")Integer teamId);
}
