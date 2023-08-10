package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Achievement {
    private Integer id;
    private String title;
    private Integer type;
    private Integer remark;
    @TableField("team_id")
    private Integer teamId;
    @TableField("file")
    private String fileUrl;
    private String description;

}
