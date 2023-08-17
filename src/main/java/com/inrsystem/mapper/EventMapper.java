package com.inrsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.Event;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Mapper
public interface EventMapper extends BaseMapper<Event> {
    @Update("UPDATE event SET remark=#{remark} WHERE remark=0 AND id=#{event_id}")
    Boolean updateEventState(@Param("remark")Integer remark, @Param("event_id") Integer eventID);
    @Select("SELECT * FROM `event` WHERE remark=0")
    List<Event> getEvents();
    @Select("SELECT * FROM `event` WHERE company_id=#{id}")
    List<Event> getEventsByCompanyId(@Param("id") Integer companyId);
    @Update("UPDATE event SET start_time=#{startTime},end_time=#{end_time} WHERE id=#{event_id}")
    Integer updateTime(@Param("startTime")Long start,@Param("endTime")Long end,@Param("price")Double price);
}
