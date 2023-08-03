package com.inrsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.Event;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface EventMapper extends BaseMapper<Event> {
    @Update("UPDATE event SET state=#{remark} WHERE remark=0 AND id=#{event_id}")
    Boolean updateEventState(@Param("remark")Integer remark, @Param("event_id") Integer eventID);
}
