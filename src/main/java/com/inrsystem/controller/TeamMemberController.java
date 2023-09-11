package com.inrsystem.controller;

import com.inrsystem.annotation.Authorized;
import com.inrsystem.dao.*;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.*;
import com.inrsystem.util.AddressChangeUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    @Resource
    private CompanyMapper companyMapper;

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

        map.put("name",teamMembers.getName());
        map.put("id",teamMembers.getId());
        Team team = teamMapper.selectById(teamId);
        achievementMap.put("team_id",team.getId());
        achievementMap.put("team_name",team.getName());
        List<Achievement> achievements = achievementMapper.selectByMap(achievementMap);
        map.put("achievements",achievements);
        if (map.isEmpty()){
            throw new LocalRunTimeException(ErrorEnum.ERROR_GET_ACHIEVEMENT_INFORMATION);
        }
       return map;
    }
    //获取团队的成员
    @GetMapping("/getTeamMembers")
    public List<TeamMembers> gerTeamMembers(@RequestAttribute("info") Map<String, Object> info){
        TeamMembers teamMembers = teamMembersMapper.selectByMap(info).get(0);
        return teamMembersMapper.getSameTeamMembers(teamMembers.getTeamId());
    }
   @PostMapping("/createTeam")
    public void creatTeam(@RequestAttribute("info") Map<String, Object> info,@RequestBody()Map<String,Object> map){
    String teamName = map.get("teamName").toString();
    if(teamMapper.getSameName(teamName)>0){
        throw new LocalRunTimeException(ErrorEnum.MULTIPLY_NAME);
    }
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
    //selectMap.put("name",teamName);
    Team team1 = teamMapper.selectByMap(selectMap).get(0);
    Integer id = team1.getId();
    List<Map<String,Object>> members = (List<Map<String, Object>>) map.get("members");
    if (members.isEmpty()){
        throw new LocalRunTimeException(ErrorEnum.ERROR_GET_MEMBER_INFORMATION);
    }
    for (Map<String,Object> m:members) {
        String name = m.get("name").toString();
        TeamMembers teamMembers = teamMembersMapper.getMembersByName(name).get(0);
        if(teamMembers.getTeamId()!=null){
            throw new LocalRunTimeException(ErrorEnum.MULTIPLY_ENTER);
        }
        else {
        Boolean aBoolean = teamMembersMapper.creatTeam(id, (Integer) m.get("team_role"), m.get("name").toString());
        if(!aBoolean){
            throw new LocalRunTimeException(ErrorEnum.ERROR_CREAT_TEAM);
        }}
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
            map.put("budget",event.getBudget());
        if (event.getState()==2){
            map.put("team_id",team_eventMapper.getTeamID(eventId));
        }
        }
        return map;
    }
    //
    @Authorized(TeamRole = 1)
    @PostMapping("/postAchievement")
    public boolean postAchievement(@RequestAttribute("info") Map<String,Object> info, @RequestBody Map<String,Object> map){
        Integer teamId = teamMembersMapper.selectByMap(info).get(0).getTeamId();
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
            achievement.setFile(map.get("file").toString());
        }
        int insert = achievementMapper.insert(achievement);
        if(insert==0){
            throw new LocalRunTimeException(ErrorEnum.ERROR_ADD_ACHIEVEMENT);
        }
        return (insert!=0)?true:false;

    }

    @GetMapping("/getEvent")
    public List<Map<String,Object>> getAllEvent(@RequestParam Map<String,Object> map1){
        Integer time = Integer.parseInt(map1.get("time").toString());
        map1.remove("time");
        List<Event> list1 = eventMapper.selectByMap(map1);
        List<Map<String,Object>> list=new ArrayList<>();
        for (Event event:list1) {
//            Map<String,Object>  map =new HashMap<>();
//            Company company = companyMapper.selectById(event.getCompanyId());
//            if (event.getState()==0){
//               map.put("company_id", event.getCompanyId());
//               map.put("event_id", event.getId());
//               map.put("event_name", event.getName());
//               map.put("description", event.getDescription());
//               map.put("state", event.getState());
//               map.put("type", event.getType());
//               map.put("budget",event.getBudget());
//               map.put("reservePrice",event.getReservePrice());
//               map.put("start_time",event.getStartTime());
//               map.put("end_time",event.getEndTime());
//               map.put("company_name",company.getName());
//              list.add(map);
//            }
            if(time==1){
                for (Event e:list1) {
                    Date d =new Date(System.currentTimeMillis());
                    if(d.after(e.getEndTime())){
                        list1.remove(e);
                    }
                }
            }
        }
        return list;
    }

    //获取中标的任务信息
    @GetMapping("/getSuccessfullyEventInformation")
    public List<Map<String,Object>> getSuccessEvent(@RequestAttribute("info") Map<String,Object> info){
     List<Map<String,Object>> list =new ArrayList<>();
        List<Team_event> statedByTeamId = team_eventMapper.getStatedByTeamId(teamMembersMapper.selectByMap(info).get(0).getTeamId());
        for (Team_event t:statedByTeamId) {
          Map<String, Object> map = new HashMap<>();
            Event event = eventMapper.selectById(t.getEventId());
            map.put("company_id",event.getCompanyId());
            map.put("event_id",event.getId());
            map.put("event_name",event.getName());
            map.put("description",event.getDescription());
            map.put("bid",t.getBid());
            if(t.getSalary()!=0){
            map.put("salary",t.getSalary());}
            map.put("start_time",event.getStartTime());
            map.put("end_time",event.getEndTime());
            list.add(map);
        }
        return list;
    }


}
