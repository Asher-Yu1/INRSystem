package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("research_teamMembers")
public class TeamMembers {
  private String account;
  private String password;
  private String name;
  private Integer id;
  @TableField("team_id")
  private Integer teamId;
  private Integer role;
  @TableField("team_role")
  private Integer teamRole;

}
