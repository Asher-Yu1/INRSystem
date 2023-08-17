package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Team_event {
    @TableField("team_id")
    private Integer teamId;
    @TableField("event_id")
    private Integer eventId;
    //报价
    private double bid;
    private Integer state;
    //酬劳
    private double salary;
    //分数
    @TableField (exist = false)
    private double  achievementScore;
}
