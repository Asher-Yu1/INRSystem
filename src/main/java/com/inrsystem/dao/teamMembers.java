package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;

public class teamMembers {
  private String account;
  private String password;
  private String name;
  private Integer id;
  @TableField("team_id")
  private Integer teamId;
  private Integer role;
  @TableField("achievement_id")
  private Integer achievementId;
}
