package com.inrsystem.dao;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Administrators  {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String account;
    private String password;
    private String email;
    private String name;
    private Integer role;


}
