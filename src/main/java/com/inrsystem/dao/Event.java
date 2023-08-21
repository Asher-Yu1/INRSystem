package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Event {
    @TableId("id")
    private Integer id;
    @TableField("company_id")
    private Integer companyId;
    private String name;
    private String description;
    //预算
    private Double budget;
    //价格
    @TableField("reservePrice")
    private double reservePrice;
    private Integer remark;
    private Integer state;
    @TableField("start_time")
    private Date startTime;
    @TableField("end_time")
    private Date endTime;
    private Integer type;
}
