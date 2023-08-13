package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Team_event {
    @TableField("team_id")
    private Integer teamId;
    @TableField("event_id")
    private Integer eventId;
    private double price;
    private Integer state;
}
