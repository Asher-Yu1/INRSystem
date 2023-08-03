package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class TeamMembers {
    private String account;
    private String password;
    @TableField("team_id")
    private Integer teamId;
    private Integer id;
    private String name;
   //0为管理员，1为公司，2为团队负责人，3为团队成员
    private Integer role;
    @TableField("achievement_id")
    private Integer achievementId;
    @TableField("team_role")
    private Integer teamRole;
}
