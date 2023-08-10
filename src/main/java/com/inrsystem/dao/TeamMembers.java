package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("research_teamMembers")
public class TeamMembers {
    private String account;
    private String password;
    @TableField("team_id")
    private Integer teamId;
    private Integer id;
    private String name;
   //0为管理员，1为公司，2为团队负责人，3为团队成员
    private Integer role;
    @TableField("team_role")
    private Integer teamRole;
}
