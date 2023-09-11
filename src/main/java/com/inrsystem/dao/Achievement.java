package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Achievement {
    private Integer id;
    private String title;
    private String description;
    private Integer type;
    @TableField("team_id")
    private Integer teamId;
    private String file;
    private Integer remark;

}
