package com.inrsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inrsystem.dao.Team_event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface Team_eventMapper extends BaseMapper<Team_event> {
    @Select("SELECT team_id FROM `team_event` WHERE event_id=#{event_id} AND state=1")
    Integer getTeamID(@Param("event_id") Integer eventId);

    @Select("SELECT state FROM `team_event` WHERE team_id=#{team_id}")
    Integer getState(@Param("team_id") Integer teamId);
    @Select("SELECT team_id FROM team_event WHERE event_id=#{event_id} And state=0")
    List<Integer> getAllTeamId(@Param("event_id") Integer eventId);
}
