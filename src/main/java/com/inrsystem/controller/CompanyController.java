package com.inrsystem.controller;

import com.inrsystem.annotation.Authorized;
import com.inrsystem.dao.Achievement;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.Event;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.AchievementMapper;
import com.inrsystem.mapper.CompanyMapper;
import com.inrsystem.mapper.EventMapper;
import com.inrsystem.mapper.Team_eventMapper;
import com.inrsystem.service.CompanyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        event.setPrice((Integer) map.get("price"));
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
      if(achievement.getFileUrl()!=null){
          map.put("file",achievement.getFileUrl());
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
              map.put("price",event.getPrice());
              map.put("state",event.getState());
              if(event.getState()==2&&team_eventMapper.getState(team_eventMapper.getTeamID(event.getId()))==1){
                  map.put("team_id",team_eventMapper.getTeamID(event.getId()));
              }
             list.add(map);
          }
          return list;
    }


}
