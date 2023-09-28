package com.inrsystem.controller;


import com.inrsystem.dao.Administrators;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.CrowdKernelComponent;
import com.inrsystem.dao.TeamMembers;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.AdministratorsMapper;
import com.inrsystem.mapper.CompanyMapper;
import com.inrsystem.mapper.TeamMembersMapper;
import com.inrsystem.service.TeamMembersService;
import com.inrsystem.util.JwtUtil;
import com.inrsystem.util.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class LoginController {

    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private CompanyMapper companyMapper;
    @Resource
    private AdministratorsMapper administratorsMapper;
    @Resource
    private TeamMembersMapper teamMembersMapper;


    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody Map<String,Object> map){
        String account = map.get("account").toString();
        boolean isAccountEmpty = account.equals("")|| account.isBlank();
        if(isAccountEmpty){
            throw new LocalRunTimeException(ErrorEnum.UNFILLED_ID);
        }
        // 判断密码是否为空
        String password =map.get("password").toString();
        boolean isPasswordEmpty = password.equals("") || password.isBlank();
        if (isPasswordEmpty) {
            throw new LocalRunTimeException(ErrorEnum.UNFILLED_PASSWORD);
        }
        Map<String, Object> user = new HashMap<>();
        List<Company> company = companyMapper.selectByMap(map);
        List<Administrators> administrators =  administratorsMapper.selectByMap(map);
        List<TeamMembers> teamMembers =  teamMembersMapper.selectByMap(map);
        if (company==null&&administrators==null&&teamMembers==null){
            throw new LocalRunTimeException(ErrorEnum.NOT_EXIST);
        }
        if(!company.isEmpty()){
            user.put("account",company.get(0).getAccount());
            user.put("name",company.get(0).getName());
            user.put("role",company.get(0).getRole());
        }
        if (!administrators.isEmpty()){
            user.put("account",administrators.get(0).getAccount());
            user.put("name",administrators.get(0).getName());
            user.put("role",administrators.get(0).getRole());
        }
        if(!teamMembers.isEmpty()){
            user.put("account",teamMembers.get(0).getAccount());
            user.put("name",teamMembers.get(0).getName());
            user.put("role",teamMembers.get(0).getRole());
        }
        // 创建token
        String token = jwtUtil.createToken(user);
        user.put("token", token);
        if(user.isEmpty()){
            throw new LocalRunTimeException(ErrorEnum.NOT_SIGN);
        }
        return user;
    }


    @PostMapping("/exit")
    public void logout(@RequestAttribute("info") Map<String, Object> info) {

        redisUtil.del(info.get("id").toString());
    }
}
