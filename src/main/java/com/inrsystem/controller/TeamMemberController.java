package com.inrsystem.controller;

import com.inrsystem.annotation.Authorized;
import com.inrsystem.dao.Achievement;
import com.inrsystem.dao.Event;
import com.inrsystem.dao.Team;
import com.inrsystem.dao.TeamMembers;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.*;
import com.inrsystem.util.AddressChangeUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/research")
@Authorized(roles = {2})
public class TeamMemberController {
    @Resource
    private TeamMembersMapper teamMembersMapper;
    @Resource
    private AchievementMapper achievementMapper;
    @Resource
    private AddressChangeUtil addressChangeUtil;
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private EventMapper eventMapper;
    @Resource
    private Team_eventMapper team_eventMapper;

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
      //  String name = info.get("name").toString();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> achievementMap = new HashMap<>();
        map.put("account",account);
        TeamMembers teamMembers = teamMembersMapper.selectByMap(map).get(0);
        Integer teamId = teamMembers.getTeamId();
        map.put("team_id",teamId);
        map.put("id",teamMembers.getId());
        Team team = teamMapper.selectById(teamId);
        achievementMap.put("team_id",team.getId());
        List<Achievement> achievements = achievementMapper.selectByMap(achievementMap);
        map.put("achievements",achievements);
        if (map.isEmpty()){
            throw new LocalRunTimeException(ErrorEnum.ERROR_GET_ACHIEVEMENT_INFORMATION);
        }
       return map;
    }
@Authorized(TeamRole = 1)
   @PostMapping("/createTeam")
    public void creatTeam(@RequestAttribute("info") Map<String, Object> info,@RequestBody()Map<String,Object> map){
    String teamName = map.get("teamName").toString();
    Map<String, Object> selectMap = new HashMap<>();
    selectMap.put("name",teamName);
    List<Team> teams = teamMapper.selectByMap(selectMap);
    if(!teams.isEmpty()){
        throw new LocalRunTimeException(ErrorEnum.NAME_EXIST);
    }
    Team team = new Team();
    team.setName(teamName);
    int insert = teamMapper.insert(team);
    if(insert==0){
        throw new LocalRunTimeException(ErrorEnum.ERROR_INSERT);
    }
    selectMap.put("name",teamName);
    Team team1 = teamMapper.selectByMap(selectMap).get(0);
    Integer id = team1.getId();
    List<Map<String,Object>> members = (List<Map<String, Object>>) map.get("members");
    if (members.isEmpty()){
        throw new LocalRunTimeException(ErrorEnum.ERROR_GET_MEMBER_INFORMATION);
    }
    for (Map<String,Object> m:members) {
        Boolean aBoolean = teamMembersMapper.creatTeam(id, (Integer) m.get("team_role"),(Integer) m.get("id"), m.get("name").toString());
        if(!aBoolean){
            throw new LocalRunTimeException(ErrorEnum.ERROR_CREAT_TEAM);
        }
    }
    }
//      //获取团队信息
//@GetMapping("/getTeamInformation")
//    public Map<String,Object> getTeamInformation(@RequestAttribute("info") Map<String,Object> info){
//    Map<String, Object> selectMap = new HashMap<>();
//    selectMap.put("account",info.get("account"));
//    //selectMap.put("name",info.get("name"));
//    TeamMembers teamMembers = teamMembersMapper.selectByMap(selectMap).get(0);
//    Integer teamId = teamMembers.getTeamId();
//    Team team = teamMapper.selectById(teamId);
//    String name = team.getName();
//    List<TeamMembers> sameTeamMembers = teamMembersMapper.getSameTeamMembers(teamId);
//    List<Achievement> achievement=new ArrayList<Achievement>();
//    for (TeamMembers member:sameTeamMembers) {
//        Integer achievementId =(Integer)member.getAchievementId();
//        Achievement achievement1 = achievementMapper.getAchievement(achievementId);
//        achievement.add(achievement1);
//    }
//    Map<String,Object>  returnMap=new HashMap<>();
//    returnMap.put("id",teamId);
//    returnMap.put("name",name);
//    returnMap.put("achievements",achievement);
//    return returnMap;
//
//}
    //获取推送过来项目的信息
    @GetMapping("/getEventInformation")
    public Map<String,Object> getEventInformation(@RequestAttribute("info") Map<String,Object> info,@RequestParam("eventId")Integer eventId){
        Map<String,Object> map =new HashMap<>();
        Event event = eventMapper.selectById(eventId);
        if (event.getRemark()==0||event.getRemark()==1){
            throw new LocalRunTimeException(ErrorEnum.NOT_REMARK_THIS_EVENT);
        }
        if(event.getRemark()==2){
        map.put("company_id",event.getCompanyId());
        map.put("event_id",eventId);
        map.put("event_name",event.getName());
        map.put("description",event.getDescription());
            map.put("price",event.getPrice());
        if (event.getState()==2){
            map.put("team_id",team_eventMapper.getTeamID(eventId));
        }
        }
        return map;
    }
    //
    @Authorized(TeamRole = 1)
    @PostMapping("/postAchievement")
    public void postAchievement(@RequestAttribute("info") Map<String,Object> info, @RequestBody ArrayList<Map<String,Object>> mapArrayList){
        Integer teamId = teamMembersMapper.selectByMap(info).get(0).getTeamId();
        for (Map<String,Object> map:mapArrayList) {
            Achievement achievement=new Achievement();
            String title = map.get("title").toString();
            String description = map.get("description").toString();
            Integer type=Integer.parseInt(map.get("type").toString());
            achievement.setTeamId(teamId);
            achievement.setTitle(title);
            achievement.setType(type);
            achievement.setDescription(description);
            achievement.setRemark(0);
            if(map.get("file").toString()!=null){
                achievement.setFileUrl(map.get("file").toString());
            }
            int insert = achievementMapper.insert(achievement);
            if(insert==0){
                throw new LocalRunTimeException(ErrorEnum.ERROR_ADD_ACHIEVEMENT);
            }
        }
    }



}
