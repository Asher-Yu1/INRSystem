package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teamMembers")
public class NormalTeamMembers {
    @TableId(type = IdType.AUTO)
    private  Long id;
    private String name;
    private  String email;
    private  Long teamId;
}
