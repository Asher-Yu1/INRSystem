package com.inrsystem.controller;

import com.inrsystem.annotation.Authorized;
import com.inrsystem.dao.*;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.*;
import com.inrsystem.service.CompanyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Double.POSITIVE_INFINITY;

@Slf4j
@RestController
@RequestMapping("/companies")
@Authorized(roles = {1})
public class CompanyController {
   @Resource
   private CompanyMapper companyMapper;
    @Resource
   private CompanyService companyService;
   @Resource
   private EventMapper eventMapper;

   @Resource
   private TeamMembersMapper teamMembersMapper;
   @Resource
   private AchievementMapper achievementMapper;
   @Resource
   private Team_eventMapper team_eventMapper;
   @Resource
   private TeamMapper teamMapper;
    @GetMapping("/test")
    public Map<String, Object> getInfo(@RequestAttribute("info") Map<String, Object> info) {
        Map<String, Object> map = new HashMap<>();
        map.put("role", info.get("role"));
        map.put("id", info.get("id"));
        return map;
    }
    @PostMapping("/postEvents")
    public Boolean postEvents(@RequestAttribute("info") Map<String, Object> info,@RequestBody()Map<String,Object> map){
        String account = info.get("account").toString();
        Company company = companyService.getCompany(account);
        Event event =new Event();
        event.setCompanyId(company.getId());
        event.setName(map.get("event_name").toString());
        event.setDescription(map.get("description").toString());
        //预算
        if(String.valueOf(map.get("price"))!=null){
        event.setBudget(Double.parseDouble(map.get("price").toString()));}
        //固定价格
        if(String.valueOf(map.get("reservePrice"))!=null){
            event.setReservePrice(Double.parseDouble(map.get("reservePrice").toString()));
        }
        int type = Integer.parseInt(map.get("type").toString());
        event.setType(type);
        Date date = new Date(System.currentTimeMillis());
        event.setStartTime(date);
        event.setEndTime(new Date(date.toString()+map.get("time")));
        event.setRemark(0);
        event.setState(0);
        int insert = eventMapper.insert(event);
       return (insert!=0)?true:false;
    }
//获取科研团队成员的科研成果
  @GetMapping("/getTeamAchievements/{team_id}")
    public Map<String,Object> getTeamAchievement(@RequestAttribute("info") Map<String, Object> info,
                                                 @PathVariable("team_id")Integer teamId){
      Achievement achievement = achievementMapper.getAchievementByTeamId(teamId);
      Map<String,Object> map =new HashMap<>();
      map.put("id",achievement.getId());
      map.put("title",achievement.getTitle());
      map.put("type",achievement.getType());
      map.put("member_id",achievement.getTeamId());
      if(achievement.getFile()!=null){
          map.put("file",achievement.getFile());
      }
      return map;
    }

//获取已发布任务列表
      @GetMapping("/getEventDetails")
    public List<Event> getEventDetails(@RequestAttribute("info") Map<String, Object> info,@RequestParam()Map<String,Object> map1){
         Company company = companyMapper.selectByMap(info).get(0);
         map1.put("company_id",company.getId());
          Integer time =Integer.parseInt(map1.get("time").toString()) ;
          map1.remove("time");
          List<Event> eventsByCompanyId = eventMapper.selectByMap(map1);
          if(time==1){
              for (Event e:eventsByCompanyId) {
                  Date d =new Date(System.currentTimeMillis());
                  if(d.after(e.getEndTime())){
                      eventsByCompanyId.remove(e);
                  }
              }
          }
//          List<Map<String,Object>> list =new ArrayList<>();
//          for (Event event:eventsByCompanyId) {
//              if(event.getRemark()==0){
//                  throw  new LocalRunTimeException(ErrorEnum.NOT_REMARK_THIS_EVENT);
//              }
//              Map<String,Object> map=new HashMap<>();
//              map.put("company_id",company.getId());
//              map.put("event_id",event.getId());
//              map.put("event_name",event.getName());
//              map.put("description",event.getDescription());
//              map.put("remark",event.getRemark());
//              if(event.getBudget()!=null){
//              map.put("price",event.getBudget());}
//              else {
//                  map.put("price",null);
//              }
//              if(event.getReservePrice() != null){
//                  map.put("reservePrice",event.getReservePrice());}
//              else {
//                  map.put("reservePrice",null);
//              }
//              map.put("state",event.getState());
//              if(event.getState()==2&&team_eventMapper.getState(team_eventMapper.getTeamID(event.getId()))==1){
//                  map.put("team_id",team_eventMapper.getTeamID(event.getId()));
//              }
//             list.add(map);
//          }
         // return list;
          return eventsByCompanyId;
    }
    //获取中标团队的信息
    @GetMapping("/getTeamInformation")
    public List<Map<String,Object>> getTeamInformation(@RequestAttribute("info")Map<String,Object> info){
        List<Map<String,Object>> list =new ArrayList<>();
        Integer id = companyMapper.selectByMap(info).get(0).getId();
        List<Event> eventsByCompanyId = eventMapper.getEventsByCompanyId(id);
        for (Event e:eventsByCompanyId) {
            if(e.getState()!=2)
                continue;
            else {
                Map<String,Object> map =new HashMap<>();
                List<Integer> allowedTeamId = team_eventMapper.getAllowedTeamId(e.getId());
                for (Integer teamId :allowedTeamId) {
                    Team team = teamMapper.selectById(teamId);
                    map.put("team_id",team.getId());
                    map.put("team_name",team.getName());
                    map.put("event_id",e.getId());
                    list.add(map);
                }
            }
        }
        return list;
    }
//获取中标的团队的信息
    @GetMapping("/getTeamDetails/{team_id}")
    public Map<String,Object> getTeamDetails(@PathVariable Integer team_id,
                                             @RequestAttribute("info")Map<String,Object> info){
        Map<String,Object> returnMap =new HashMap<>();
        List<TeamMembers> sameTeamMembers = teamMembersMapper.getSameTeamMembers(team_id);
        List<Map<String,Object>>  members=new ArrayList<>();
        for (TeamMembers t:sameTeamMembers) {
            Map<String,Object> map1=new HashMap<>();
            map1.put("name",t.getName());
            map1.put("role",t.getTeamRole());
            members.add(map1);
        }

        Team team = teamMapper.selectById(team_id);
        Map<String, Object> achievementMap = new HashMap<>();
        achievementMap.put("team_id",team.getId());
        List<Achievement> achievements = achievementMapper.selectByMap(achievementMap);
        if (achievements.isEmpty()){
            throw new LocalRunTimeException(ErrorEnum.ERROR_GET_ACHIEVEMENT_INFORMATION);
        }

        returnMap.put("members",members);
        returnMap.put("achievements",achievements);
        return returnMap;
    }

    @GetMapping("/getDetails")
    public Map<String,Object> getDetails(@RequestAttribute("info")Map<String,Object> info){
        Company company = companyMapper.selectByMap(info).get(0);
        Map<String,Object> map=new HashMap<>();
        map.put("id",company.getId());
        map.put("name",company.getName());
        return map;
    }
}
