package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team_event {

    @TableField("team_id")
    private Long teamId;

    @TableField("event_id")
    private Long eventId;
    //报价
    private double bid;
    private Integer state;
    //酬劳
    private double salary;
    //分数
    @TableField (exist = false)
    private double  achievementScore;
}
