package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Company {
    private String account;
    private String password;
    private Integer id;
    private String name;
    @TableField("event_id")
    private Integer eventId;
    @TableField("team_id")
    private Integer teamId;
    private Integer role;
}
