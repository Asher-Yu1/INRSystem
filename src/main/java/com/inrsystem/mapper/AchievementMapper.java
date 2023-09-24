package com.inrsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inrsystem.dao.Achievement;
import org.apache.ibatis.annotations.*;
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
    Integer updateAchievementRemark(@Param("remark")Integer remark, @Param("achievement_id") Long achievementId);

    @Select("SELECT * FROM achievement WHERE team_id=#{team_Id} AND type=#{type}")
    List<Achievement> getAchievementByTeamIdAndType (@Param("team_Id") Long team_Id, @Param("type") Integer type);
    //insert


}
