package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;

public class company {
    private String account;
    private String password;
    private Integer id;
    private String name;
    @TableField("event_id")
    private Integer eventId;
    @TableField("team_id")
    private Integer teamId;
}
