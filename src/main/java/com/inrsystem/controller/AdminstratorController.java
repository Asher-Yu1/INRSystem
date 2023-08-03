package com.inrsystem.controller;

import com.inrsystem.annotation.Authorized;
import com.inrsystem.dao.Event;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.EventMapper;
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
    @GetMapping("/test")
    public Map<String, Object> getInfo(@RequestAttribute("info") Map<String, Object> info) {
        Map<String, Object> map = new HashMap<>();
        map.put("role", info.get("role"));
        map.put("id", info.get("id"));
        return map;
    }
    @GetMapping("/getEventInformation")
    public List<Map<String,Object>> getEventInformation(@RequestAttribute("info") Map<String, Object> info){
        List<Map<String, Object>> list = new ArrayList<>();
        List<Event> events = eventMapper.selectList(null);
        if(events.isEmpty()){
            throw new LocalRunTimeException(ErrorEnum.EVENT_NOT_FIND);
        }
        for (Event e:events) {
            Map<String,Object> map =new HashMap<>();
            map.put("company_id",e.getCompanyId());
            map.put("team_id",e.getTeamId());
            map.put("event_id",e.getId());
            map.put("event_name",e.getName());
            map.put("description",e.getDescription());
            map.put("price",e.getPrice());
            map.put("remark",e.getRemark());
            map.put("state",e.getState());
            list.add(map);
        }
        return list;
    }
    //审核公司任务
    @PostMapping("/auditEvents")
    public void auditEvents(@RequestAttribute("info") Map<String, Object> info,@RequestBody()Map<String,Object> map){
        Integer eventId = (Integer) map.get("event_id");
        Integer remark = (Integer) map.get("remark");
        Boolean aBoolean = eventMapper.updateEventState(remark, eventId);
        if (aBoolean==false){
            throw new LocalRunTimeException(ErrorEnum.ERROR_REMARK);
        }
    }
}
