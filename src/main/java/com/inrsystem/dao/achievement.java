package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.TableField;

public class achievement {
    private Integer id;
    @TableField("FileUrl")
    private String fileUrl;
    private Integer remark;
}
