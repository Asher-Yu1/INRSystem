package com.inrsystem.controller;

import com.inrsystem.dao.Event;
import com.inrsystem.dao.TeamMembers;
import com.inrsystem.dao.Team_event;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.EventMapper;
import com.inrsystem.mapper.TeamMembersMapper;
import com.inrsystem.mapper.Team_eventMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TenderController {
    @Resource
    private TeamMembersMapper teamMembersMapper;
    @Resource
    private Team_eventMapper team_eventMapper;
    @Resource
    private EventMapper eventMapper;
    //投标
    @PostMapping("/tender/{event_id}")
    public Boolean tender(@RequestAttribute("info") Map<String,Object> info,@PathVariable("event_id") Long eventId
                       , @RequestBody Map<String,Object> map){
       //通过token信息获取个人信息->team_id
        TeamMembers teamMembers = teamMembersMapper.selectByMap(info).get(0);
        int insert=0;
        if(teamMembers.getTeamId()==null){
            throw new LocalRunTimeException(ErrorEnum.AUTHORITY_ERROR);
        }
        else {
            eventMapper.setState(1,eventId);
            //插入team_event表
            Team_event team_event = new Team_event();
            Long teamId = teamMembers.getTeamId();
            Event event = eventMapper.selectById(eventId);
            Integer bid = Integer.parseInt(map.get("bid").toString());
            team_event.setEventId(eventId);
            team_event.setTeamId(teamId);
            //有一种交易方式没有bid->2
            if(event.getType()!=2)
                team_event.setBid(bid);
            team_event.setState(0);
            insert = team_eventMapper.insert(team_event);
            if(insert==0){
                throw new LocalRunTimeException(ErrorEnum.ERROR_INSERT);
            }
        }
        return(insert!=0)?true:false;
    }

}
