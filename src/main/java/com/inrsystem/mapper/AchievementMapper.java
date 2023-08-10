package com.inrsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inrsystem.dao.Achievement;
import com.inrsystem.dao.Administrators;
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
public interface AchievementMapper extends BaseMapper<Achievement> {
    @Select("SELECT * FROM achievement WHERE id=#{achievementId}")
    Achievement getAchievement (@Param("achievementId")Integer achievementId);
    @Select("SELECT * FROM achievement WHERE team_id=#{team_Id}")
    Achievement getAchievementByTeamId (@Param("team_Id")Integer team_Id);
    @Update("UPDATE achievement SET remark=#{remark} WHERE remark=0 AND id=#{achievement_id}")
    Boolean updateAchievementRemark(@Param("remark")Integer remark, @Param("achievement_id") Integer achievementId);
}
