package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Event {
    private Integer id;
    @TableField("company_id")
    private Integer companyId;
    @TableField("team_id")
    private Integer teamId;
    private String name;
    private String description;
    private Integer price;
    private Integer remark;
    private Integer state;
}