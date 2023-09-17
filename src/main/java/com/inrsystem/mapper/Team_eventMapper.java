package com.inrsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inrsystem.dao.Team_event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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

    @Select("SELECT * FROM `team_event` WHERE event_id=#{event_id} AND state=0")
    List<Team_event> getAllByEventId(@Param("event_id") Integer eventId);
@Update("UPDATE team_event SET salary=#{salary} ,state=1 WHERE team_id=#{team_id} AND event_id=#{event_id} AND bid=#{bid}")
    Integer winingTeam (@Param("salary")double salary,@Param("team_id")Integer teamID,
                        @Param("event_id")Integer eventId,@Param("bid")double bid);

    @Select("SELECT * FROM `team_event` WHERE team_id=#{team_id} AND state=1")
    List<Team_event> getStatedByTeamId(@Param("team_id") Integer teamId);

    @Select("SELECT team_id FROM team_event WHERE event_id=#{event_id} And state=1")
    List<Integer> getAllowedTeamId(@Param("event_id") Integer eventId);

    @Select("SELECT * FROM team_event WHERE event_id=#{event_id} And team_id=#{team_id}")
    Team_event getByEventIdAndTeamId(@Param("event_id") Integer eventId,@Param("team_id")Integer teamId);
}
