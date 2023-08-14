package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class Event {
    private Integer id;
    @TableField("company_id")
    private Integer companyId;
    private String name;
    private String description;
    private Integer price;
    private Integer remark;
    private Integer state;
    @TableField("start_time")
    private Date startTime;
    @TableField("end_time")
    private Date endTime;
    private Integer type;
}
