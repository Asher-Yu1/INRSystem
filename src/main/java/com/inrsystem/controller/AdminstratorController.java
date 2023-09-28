package com.inrsystem.controller;

import com.inrsystem.annotation.Authorized;
import com.inrsystem.dao.Achievement;
import com.inrsystem.dao.Event;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.AchievementMapper;
import com.inrsystem.mapper.CompanyMapper;
import com.inrsystem.mapper.EventMapper;
import com.inrsystem.mapper.TeamMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/adminstrators")
@Authorized(roles = {0})
public class AdminstratorController {
    @Resource
    private EventMapper eventMapper;
    @Resource
    private AchievementMapper achievementMapper;
    @Resource
    private CompanyMapper companyMapper;
    @Resource
    private TeamMapper teamMapper;
    @GetMapping("/test")
    public Map<String, Object> getInfo(@RequestAttribute("info") Map<String, Object> info) {
        Map<String, Object> map = new HashMap<>();
        map.put("role", info.get("role"));
        map.put("id", info.get("id"));
        return map;
    }
    @GetMapping("/getEventInformation/{remark}")
    public List<Map<String,Object>> getEventInformation(@RequestAttribute("info") Map<String, Object> info,
                                                        @PathVariable("remark")Integer remark){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String,Object> selectMap =new HashMap<>();
        selectMap.put("remark",remark);
        List<Event> events = eventMapper.selectByMap(selectMap);
        if(events.isEmpty()){
            throw new LocalRunTimeException(ErrorEnum.EVENT_NOT_FIND);
        }
        for (Event e:events) {
            Map<String,Object> map =new HashMap<>();
            map.put("company_id",e.getCompanyId());
            map.put("company_name",companyMapper.selectById(e.getCompanyId()).getName());
            map.put("event_id",e.getId());
            map.put("event_name",e.getName());
            map.put("description",e.getDescription());
            map.put("price",e.getBudget());
            map.put("remark",e.getRemark());
            map.put("state",e.getState());
            list.add(map);
        }
        return list;
    }
    //审核公司任务
    @PostMapping("auditEvents/{id}")
    public Boolean auditEvents(@RequestAttribute("info") Map<String, Object> info,@PathVariable("id")Integer eventId,
                            @RequestBody()Map<String,Object> map){
        Integer remark = (Integer) map.get("remark");
        Integer number = eventMapper.updateEventState(remark, eventId);
        if (number==0){
            throw new LocalRunTimeException(ErrorEnum.ERROR_REMARK);
        }
        return (number!=0)?true:false;
    }
   //审核成果
    @PostMapping("/auditAchievement/{id}")
    public Boolean auditAchievement(@RequestAttribute("info") Map<String, Object> info,@PathVariable("id")Long achievementId,
                                    @RequestBody()Map<String,Object> map){
        int remark = Integer.parseInt(map.get("remark").toString());
        Integer number = achievementMapper.updateAchievementRemark(remark, achievementId);
        if (number==0){
            throw new LocalRunTimeException(ErrorEnum.ERROR_REMARK);
        }
        return (number!=0)?true:false;
    }

    @GetMapping("/getAchievementInformation/{remark}")
    public List<Map<String,Object>> getAchievement(@RequestAttribute("info") Map<String, Object> info,@PathVariable("remark")Integer remark){
        List<Map<String,Object>> l =new ArrayList<>();
        Map<String,Object> selectMap =new HashMap<>();
        selectMap.put("remark",remark);
        List<Achievement> achievements = achievementMapper.selectByMap(selectMap);
        for (Achievement a:achievements) {
            Map<String,Object> map =new HashMap<>();
            map.put("achievement",a);
            map.put("team_name",teamMapper.selectById(a.getTeamId()).getName());
            l.add(map);
        }
        return l ;
    }
}
