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
public class Achievement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Integer type;
    @TableField("team_id")
    private Long teamId;
    private String file;
    private Integer remark;

}
