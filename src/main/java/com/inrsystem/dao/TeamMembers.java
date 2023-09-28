package com.inrsystem.dao;

import cn.crowdos.kernel.constraint.Condition;
import cn.crowdos.kernel.resource.AbstractParticipant;
import cn.crowdos.kernel.resource.ability;
import cn.crowdos.kernel.wrapper.DateCondition;
import cn.crowdos.kernel.wrapper.IntegerCondition;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("research_teamMembers")
public class TeamMembers extends AbstractParticipant{

  @TableId(type = IdType.AUTO)
  private Long id;
  private String account;
  private String password;
  private String name;

  private String email;
  @TableField("team_id")
  private Long teamId;
  private Integer role;

  @Override
  public boolean hasAbility(Class<? extends Condition> aClass) {
    return false;
  }

  @Override
  public Condition getAbility(Class<? extends Condition> aClass) {
    return null;
  }
}
