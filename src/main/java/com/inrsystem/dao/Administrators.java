package com.inrsystem.dao;


import cn.crowdos.kernel.constraint.Condition;
import cn.crowdos.kernel.resource.AbstractParticipant;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Administrators extends AbstractParticipant {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String account;
    private String password;
    private String email;
    private String name;
    private Integer role;


    @Override
    public boolean hasAbility(Class<? extends Condition> aClass) {
        return false;
    }

    @Override
    public Condition getAbility(Class<? extends Condition> aClass) {
        return null;
    }
}
