package com.inrsystem.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Options;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;

}
