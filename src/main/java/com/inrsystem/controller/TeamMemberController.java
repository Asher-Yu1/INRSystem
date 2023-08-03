package com.inrsystem.controller;

import com.inrsystem.annotation.Authorized;
import com.inrsystem.dao.Achievement;
import com.inrsystem.dao.TeamMembers;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.AchievementMapper;
import com.inrsystem.mapper.TeamMembersMapper;
import com.inrsystem.service.TeamMembersService;
import com.inrsystem.util.AddressChangeUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/research")
@Authorized(roles = {2},TeamRole = {0})
public class TeamMemberController {
    @Resource
    private TeamMembersMapper teamMembersMapper;
    @Resource
    private AchievementMapper achievementMapper;
    @Resource
    private AddressChangeUtil addressChangeUtil;
    @GetMapping("/test")
    public Map<String, Object> getInfo(@RequestAttribute("info") Map<String, Object> info) {
        Map<String, Object> map = new HashMap<>();
        map.put("role", info.get("role"));
        map.put("id", info.get("id"));
        return map;
    }
    //获取个人信息（专利/科研成果）
    @GetMapping("/getInformation")
    public Map<String,Object> getPersonInformation(@RequestAttribute("info") Map<String, Object> info){
        String account = info.get("account").toString();
        String name = info.get("name").toString();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> achievementMap = new HashMap<>();
        TeamMembers teamMembers = teamMembersMapper.selectByMap(map).get(0);
        map.put("team_id",teamMembers.getTeamId());
        map.put("id",teamMembers.getId());
        Integer achievementId = teamMembers.getAchievementId();
        achievementMap.put("id",achievementId);
        List<Achievement> achievements = achievementMapper.selectByMap(achievementMap);
        map.put("achievements",achievements);
        if (map.isEmpty()){
            throw new LocalRunTimeException(ErrorEnum.ERROR_GET_PERSONAL_INFORMATION);
        }
       return map;
    }

}
