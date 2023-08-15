package com.inrsystem.controller;

import com.inrsystem.SimHash;
import com.inrsystem.dao.Achievement;
import com.inrsystem.dao.Event;
import com.inrsystem.mapper.AchievementMapper;
import com.inrsystem.mapper.EventMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class textualSimilarityController {
    @Resource
    private AchievementMapper achievementMapper;
    @Resource
    private EventMapper eventMapper;

    //获取匹配到的team_id
    @PostMapping("/analyse")
    public List<Integer> getAllowedTeam(@RequestBody Map<String,Object> map ) {
        Integer eventId = Integer.parseInt(map.get("event_id").toString());
        Event event = eventMapper.selectById(eventId);
        List<Integer> teamIds = (List<Integer>) map.get("teamIds");
        List<Double> scores =new LinkedList<>();
        List<Integer> returnId =new LinkedList<>();
        List<Map<String,Object>> teamScores =new ArrayList<>();
        for (Integer teamId:teamIds) {
            Map<String,Object> teamScore =new HashMap<>();
            double score = getScore(event, teamId);
            scores.add(score);
            teamScore.put("team_id",teamId);
            teamScore.put("score",score);
            teamScores.add(teamScore);
        }
        Double maxScore = getMaxScore(scores);
        for (Map<String,Object> m:teamScores) {
            double score = Double.parseDouble(m.get("score").toString());
            if(score==maxScore)
                returnId.add(Integer.parseInt(m.get("team_id").toString()));
        }
        return returnId;
    }


    //得到分数
    private double getScore(Event event, Integer teamId) {
        double score = 0;
        //0为论文*0.5，1为专利*0.7，2为项目*1
        for (int i = 0; i < 3; i++) {
            Achievement achievement = achievementMapper.getAchievementByTeamIdAndType(teamId, i);
            SimHash hash1 = new SimHash(achievement.getDescription(), 64);
            double semblance = hash1.getSemblance(new SimHash(event.getDescription(), 64));
            if (i == 0)
                score += semblance / 2;
            if (i == 1)
                score += semblance * 7 / 10;
            if (i == 2)
                score += semblance * 1;
        }
        return score;
    }

    //比较大小
    private Double getMaxScore(List<Double> list){
        return Collections.max(list);

    }



}

