package com.inrsystem.dao;
import cn.crowdos.kernel.resource.Task;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
public class Event  {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("company_id")
    private Integer companyId;
    private String name;
    private String description;
    //预算
    private Double budget;
    //价格
    @TableField("reservePrice")
    private Double reservePrice;
    private Integer remark;
    private Integer state;
    @JsonFormat(pattern = "yyyy-mm-dd hh:mm:ss",timezone = "GMT+8")
    @TableField("start_time")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-mm-dd hh:mm:ss",timezone = "GMT+8")
    @TableField("end_time")
    private Date endTime;
    private Integer type;
    private Integer isEmailed;
}
