package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Achievement {
    private Integer id;
    @TableField("FileUrl")
    private String fileUrl;
    private Integer remark;
}
