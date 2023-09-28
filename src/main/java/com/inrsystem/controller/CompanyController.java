package com.inrsystem.controller;

import com.inrsystem.annotation.Authorized;
import com.inrsystem.dao.*;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.*;
import com.inrsystem.service.CompanyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/companies")
@Authorized(roles = {1})
public class CompanyController {
    final CrowdKernelComponent crowdKernelComponent;
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
   @Resource
   private NormalTeamMembersMapper normalTeamMembersMapper;

    public CompanyController(CrowdKernelComponent crowdKernelComponent) {
        this.crowdKernelComponent = crowdKernelComponent;
    }

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
        if(map.get("price")!=null){
        event.setBudget(Double.parseDouble(map.get("price").toString()));}
        //固定价格
        if(map.get("reservePrice")!=null){
            event.setReservePrice(Double.parseDouble(map.get("reservePrice").toString()));
        }
        int type = Integer.parseInt(map.get("type").toString());
        event.setType(type);
        Date date = new Date(System.currentTimeMillis());
        event.setStartTime(new java.sql.Date(date.getTime()));
        // 获取当前时间
        Date date1 = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date1);
        // 把日期往后增加一天,整数  往后推,负数往前移动
        calendar.add(Calendar.DATE, 1*Integer.parseInt(map.get("time").toString()));
        // 这个时间就是日期往后推一天的结果
        date1 = calendar.getTime();

        event.setEndTime( new java.sql.Date(date1.getTime()));
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
      map.put("team_id",achievement.getTeamId());
      if(achievement.getFile()!=null){
          map.put("file",achievement.getFile());
      }
      return map;
    }

//获取已发布任务列表
      @GetMapping("/getEventDetails")
    public List<Map<String,Object>> getEventDetails(@RequestAttribute("info") Map<String, Object> info
              ,@RequestParam()Map<String,Object> map1){
          List<Map<String,Object>> list=new ArrayList<>();
         Company company = companyMapper.selectByMap(info).get(0);
         map1.put("company_id",company.getId());
          Integer time =Integer.parseInt(map1.get("time").toString()) ;
          map1.remove("time");
          List<Event> eventsByCompanyId = eventMapper.selectByMap(map1);
          if(time==1){
              for (Event e:eventsByCompanyId) {
                  if(eventMapper.getTime().after(e.getEndTime())){
                      eventsByCompanyId.remove(e);
                  }
              }}
          for (Event e:eventsByCompanyId) {
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
                    Team_event byEventIdAndTeamId = team_eventMapper.getByEventIdAndTeamId(e.getId(), teamId);
                    map.put("salary",byEventIdAndTeamId.getSalary());
                    map.put("bid",byEventIdAndTeamId.getBid());
                    map.put("team_name",team.getName());
                    map.put("event_id",e.getId());
                    map.put("event_name",e.getName());
                    list.add(map);
                }
            }
        }
        return list;
    }
//获取中标的团队的信息
    @GetMapping("/getTeamDetails/{team_id}")
    public Map<String,Object> getTeamDetails(@PathVariable Long team_id,
                                             @RequestAttribute("info")Map<String,Object> info){
        Map<String,Object> returnMap =new HashMap<>();
        List<NormalTeamMembers> normalTeamMembers = normalTeamMembersMapper.getSameTeamMembers(team_id);
        List<Map<String,Object>>  members=new ArrayList<>();
        for (NormalTeamMembers t:normalTeamMembers) {
            Map<String,Object> map1=new HashMap<>();
            map1.put("name",t.getName());
           map1.put("email",t.getEmail());
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
