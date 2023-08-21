package com.inrsystem.controller;

import com.inrsystem.annotation.Authorized;
import com.inrsystem.dao.Achievement;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.Event;
import com.inrsystem.dao.Team;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.*;
import com.inrsystem.service.CompanyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void postEvents(@RequestAttribute("info") Map<String, Object> info,@RequestBody()Map<String,Object> map){
        String account = info.get("account").toString();
        Company company = companyService.getCompany(account);
        Event event =new Event();
        event.setCompanyId(company.getId());
        event.setName(map.get("event_name").toString());
        event.setDescription(map.get("description").toString());
        //预算
        if(map.get("price").toString()!=null){
        event.setBudget(Double.parseDouble(map.get("price").toString()));}
        //固定价格
        if(map.get("reservePrice").toString()!=null){
            event.setReservePrice(Double.parseDouble(map.get("reservePrice").toString()));
        }
        if(map.get("price").toString()!=null&&map.get("reservePrice").toString()==null){
            event.setType(0);
        }
        if(map.get("price").toString()==null&&map.get("reservePrice").toString()==null){
            event.setType(1);
        }
        if(map.get("price").toString()==null&&map.get("reservePrice").toString()!=null){
            event.setType(2);
        }
        event.setRemark(0);
        event.setState(0);
        eventMapper.insert(event);
    }
//获取科研团队成员的科研成果
  @GetMapping("/getTeamAchievements")
    public Map<String,Object> getTeamAchievement(@RequestAttribute("info") Map<String, Object> info,@RequestParam("team_id")Integer teamId){
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
    public List<Map<String,Object>> getEventDetails(@RequestAttribute("info") Map<String, Object> info){
         Company company = companyMapper.selectByMap(info).get(0);
          List<Event> eventsByCompanyId = eventMapper.getEventsByCompanyId(company.getId());
          List<Map<String,Object>> list =new ArrayList<>();
          for (Event event:eventsByCompanyId) {
              if(event.getRemark()==0){
                  throw  new LocalRunTimeException(ErrorEnum.NOT_REMARK_THIS_EVENT);
              }
              Map<String,Object> map=new HashMap<>();
              map.put("company_id",company.getId());
              map.put("event_id",event.getId());
              map.put("event_name",event.getDescription());
              map.put("remark",event.getRemark());
              map.put("price",event.getBudget());
              map.put("state",event.getState());
              if(event.getState()==2&&team_eventMapper.getState(team_eventMapper.getTeamID(event.getId()))==1){
                  map.put("team_id",team_eventMapper.getTeamID(event.getId()));
              }
             list.add(map);
          }
          return list;
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


}
