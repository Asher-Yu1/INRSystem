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
    @Resource
    private NormalTeamMembersMapper normalTeamMembersMapper;

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
        Long teamId = teamMembers.getTeamId();
        map.put("team_id",teamId);
        map.put("name",teamMembers.getName());
        map.put("id",teamMembers.getId());
        map.put("team_name",teamMapper.selectById(teamId).getName());
        Team team = teamMapper.selectById(teamId);
        achievementMap.put("team_id",team.getId());
        List<Achievement> achievements = achievementMapper.selectByMap(achievementMap);
        map.put("achievements",achievements);
        if (map.isEmpty()){
            throw new LocalRunTimeException(ErrorEnum.ERROR_GET_ACHIEVEMENT_INFORMATION);
        }
       return map;
    }
    //获取团队的成员
    @GetMapping("/getTeamMembers")
    public List<NormalTeamMembers> gerTeamMembers(@RequestAttribute("info") Map<String, Object> info){
        TeamMembers teamMembers = teamMembersMapper.selectByMap(info).get(0);
        return normalTeamMembersMapper.getSameTeamMembers(teamMembers.getTeamId());
    }
//增加学员
   @PostMapping ("/addMembers")
    public void creatTeam(@RequestAttribute("info") Map<String, Object> info,@RequestBody()Map<String,Object> map){
       TeamMembers teamMembers = teamMembersMapper.selectByMap(info).get(0);
       Long teamId = teamMembers.getTeamId();
       List<Map<String,Object>> members = (List<Map<String, Object>>) map.get("members");
       for (Map<String,Object> m :members) {
           Map<String,Object> map1 =new HashMap<>();
           map1.put("name",m.get("name").toString());
           List<NormalTeamMembers> normalTeamMembers1 = normalTeamMembersMapper.selectByMap(map1);
           if(!normalTeamMembers1.isEmpty()){
               continue;
           }
           String name = m.get("name").toString();
           String email = m.get("email").toString();
           NormalTeamMembers normalTeamMembers = new NormalTeamMembers();
           normalTeamMembers.setName(name);
           normalTeamMembers.setEmail(email);
           normalTeamMembers.setTeamId(teamId);
           int insert = normalTeamMembersMapper.insert(normalTeamMembers);
           if(insert==0){
               throw new LocalRunTimeException(ErrorEnum.ERROR_INSERT);
           }
       }
    }
//删除成员
    @PostMapping ("/deleteMembers/{id}")
    public Boolean deleteMembers(@RequestAttribute("info") Map<String, Object> info,@PathVariable("id")Integer id){
        int i = normalTeamMembersMapper.deleteById(id);
        return i!=0?true:false;
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
    public Map<String,Object> getEventInformation(@RequestAttribute("info") Map<String,Object> info,
                                                  @RequestParam("eventId")Integer eventId){
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
            map.put("team_name",teamMapper.selectById(team_eventMapper.getTeamID(eventId)).getName());
        }
        }
        return map;
    }
    //

    @PostMapping("/postAchievement")
    public boolean postAchievement(@RequestAttribute("info") Map<String,Object> info, @RequestBody Map<String,Object> map){
        if(teamMembersMapper.selectByMap(info).isEmpty()){
            return false;
        }
        Long teamId = teamMembersMapper.selectByMap(info).get(0).getTeamId();
        Achievement achievement=new Achievement();
        String title = map.get("title").toString();
        String description = map.get("description").toString();
        Integer type=Integer.parseInt(map.get("type").toString());
        achievement.setId(null);
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

            if(time==1){
                for (Event e:list1) {
                    Date d =new Date(System.currentTimeMillis());
                    if(d.after(e.getEndTime())){
                        list1.remove(e);
                    }
                }
            }
            for (Event e:list1) {
            Map<String,Object> map=new HashMap<>();
                String end = e.getEndTime().toInstant().toString();
                String s = e.getStartTime().toInstant().toString();
                map.put("end",end);
                map.put("start",s);
                map.put("event",e);
            map.put("company_name",companyMapper.selectById(e.getCompanyId()).getName());
            list.add(map);}

        return list;
    }

    //获取中标的任务信息
    @GetMapping("/getSuccessfullyEventInformation/{state}")
    public List<Map<String,Object>> getSuccessEvent(@RequestAttribute("info") Map<String,Object> info,
                                                    @PathVariable("state")Integer state){
     List<Map<String,Object>> list =new ArrayList<>();
     List<Team_event> statedByTeamId = team_eventMapper.getStatedByTeamId(teamMembersMapper.selectByMap(info).get(0)
                     .getTeamId(),state);
        for (Team_event t:statedByTeamId) {
          Map<String, Object> map = new HashMap<>();
            Event event = eventMapper.selectById(t.getEventId());
            map.put("event",event);
            if(state==1){
            map.put("salary",t.getSalary());}
            map.put("bid",t.getBid());
            map.put("company_name",companyMapper.selectById(event.getCompanyId()).getName());
            list.add(map);
        }

        return list;
    }


}
