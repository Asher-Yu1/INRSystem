package com.inrsystem.service.impl;

import com.inrsystem.SimHash;
import com.inrsystem.dao.*;
import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;
import com.inrsystem.mapper.*;
import com.inrsystem.service.CrowdOsAlgorithmService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CrowdOsAlgorithmServiceImpl implements CrowdOsAlgorithmService {

    @Resource
    private AchievementMapper achievementMapper;
    @Resource
    private EventMapper eventMapper;
    @Resource
    private Team_eventMapper team_eventMapper;
    @Resource
    private TeamMembersMapper teamMembersMapper;
    @Resource
    private JavaMailSender javaMailSender;

    @Override
    public void getAllowedTeam() throws MessagingException {

        log.info("调用算法接口，获取竞标获胜者");
        //获取数据库中尚未中标，且时间超过当前时间
        List<Event> allEndedEvents = eventMapper.getAllEndedEvents( eventMapper.getTime());
        for (Event e:allEndedEvents) {
            Long eventId = e.getId();
            Event event = eventMapper.selectById(eventId);
            if(eventMapper.getTime().before(e.getEndTime())){
                throw new LocalRunTimeException(ErrorEnum.NOT_END);}
            if(eventMapper.getTime().after(e.getEndTime())&&event.getIsEmailed()==0){
                //根据任务id获取竞标团队
                List<Team_event> team_events=team_eventMapper.getAllByEventId(eventId);
                //基于文本匹配算法设置科研团队对应能力指数
                team_events.forEach(team_event -> team_event.setAchievementScore(getScore(event, team_event.getTeamId())));
                //基于任务类型执行拍卖算法
                List<Team_event> team_events1 = auctionByTaskType(event, team_events);
                //team_event设置成功竞标
                team_eventMapper.winingTeam(team_events1.get(0).getSalary(),team_events1.get(0).getTeamId(),
                        team_events1.get(0).getEventId(),team_events1.get(0).getBid());
                for (Team_event t:team_events) {
                    if(t==team_events1.get(0)){
                        continue;
                    }
                    team_eventMapper.losingTeam(t.getTeamId(),eventId);
                }
                //event表中设置成功竞标
                eventMapper.setState(2,eventId);
                //向团队成员发邮件
                List<String> emails=new ArrayList<>();
                List<TeamMembers> sameTeamMembers = teamMembersMapper.getSameTeamMembers(team_events1.get(0).getEventId());
                for (TeamMembers t:sameTeamMembers) {
                    emails.add(t.getEmail());
                }
                if(emails.isEmpty()){
                    throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR);
                }
                sendMail(emails);
                eventMapper.setIsEmailed(1,eventId);
            }

        }

    }

    // 基于任务类型执行不同的拍卖逻辑
    private List<Team_event> auctionByTaskType(Event event, List<Team_event> team_events){
        int auctionType=event.getType();
        return switch (auctionType) {
            case 0 -> bfdauction(event, team_events);
            case 1 -> vcgauction(event, team_events);
            case 2 -> maxauction(event, team_events);
            default -> null;
        };

    }

    /**
     * list 只有一个对象 值选择一个科研团队 固定价格  交易
     * @param event
     * @param team_events
     * @return
     */
    private List<Team_event> maxauction(Event event, List<Team_event> team_events) {
        Team_event winner=
                team_events.stream().max((o1, o2) -> (int) (o1.getAchievementScore()-o2.getAchievementScore())).get();
        winner.setState(1);
        winner.setSalary(event.getReservePrice());
        return List.of(winner);
    }

    /**
     * 选择一个科研团队实现效用最大化 没有成本限制的竞标 vcg机制
     * @param event
     * @param team_events
     * @return
     */
    private List<Team_event> vcgauction(Event event, List<Team_event> team_events) {
        List<Team_event> winners=new ArrayList<>();
        Team_event bestWin=null;
        double maxWelfare=Integer.MIN_VALUE;
        //计算得到最大社会福利的科研团队
        for (Team_event team_event:team_events){
            double welfare=100*Math.log(10*team_event.getAchievementScore())-team_event.getBid();
            if (welfare>maxWelfare){
                maxWelfare=welfare;
                bestWin=team_event;
            }
        }
        //计算该科研团队不存在时的最大社会福利，并作为支付价格整体类似于二价拍卖
        double TmpMaxWelfare=Integer.MIN_VALUE;
        for (Team_event team_event:team_events){
            if (team_event.equals(bestWin)){
                continue;
            }
            double welfare=100*Math.log(10*team_event.getAchievementScore())-team_event.getBid();
            TmpMaxWelfare=Math.max(TmpMaxWelfare,welfare);

        }
        double payMent=TmpMaxWelfare;
        bestWin.setSalary(payMent);
        winners.add(bestWin);
        return winners;
    }

    /**
     * 带有预算的效用最大化拍卖
     * @param event
     * @param team_events
     * @return 获胜者 状态为1 并设置了报酬
     */
    private List<Team_event> bfdauction(Event event, List<Team_event> team_events) {

        //初始化支付
        HashMap<Integer,Double> salrys=new HashMap<>();
        double budget= event.getBudget();
        // 过滤报价大于预算的投标者
        List<Team_event> bidders =
                team_events.stream().filter(team_event -> team_event.getBid() < event.getBudget()).collect(Collectors.toList());
        List<Team_event> biddderCopy=new ArrayList<>(bidders);
        List<Team_event> winners=new ArrayList<>();
        //设置随机数实现随机机制
        double r=Math.random();
        //40的概率选择具有最大效用的科研团队并设置支付报酬为预算
        if (r<0.4){
            Team_event winner=bidders.stream().max(new Comparator<Team_event>() {
                @Override
                public int compare(Team_event o1, Team_event o2) {
                    return (int) (o2.getAchievementScore()-o1.getAchievementScore());
                }
            }).get();
            winner.setSalary(event.getBudget());
            winners.add(winner);
            return winners;
        }
        // 60的概率执行带有预算的子模最大化贪心算法
        double max=0;
        Team_event maxTeam=null;
        //计算出具有最大平均边际效用的科研团队
        for (Team_event team_event:bidders){
            double comUitity=100*Math.log(10*team_event.getAchievementScore())/team_event.getBid();
            if (comUitity>max){
                maxTeam=team_event;
                max=comUitity;
            }
        }
        if (maxTeam==null){
            return winners;
        }
        //判断是否满足预算约束
        boolean flag=maxTeam.getBid()<budget/2;
        // 循环计算满足预算约束的平均边际效用最大的科研团队添加获胜者集合中
        while (bidders.size()>0&&flag){
            //更新获胜者集合和投标者集合
            winners.add(maxTeam);
            bidders.remove(maxTeam);
            double pre=compute(winners);
            max=0;
            maxTeam=null;
            double curUit=0;
            // 计算平均边际效用最大的科研团队
            for (Team_event team_event:bidders){
                curUit=compute(winners,team_event);
                double marignUitity=(curUit-pre)/team_event.getBid();
                if (marignUitity>max){
                    max=marignUitity;
                    maxTeam=team_event;
                }
            }
            double maxCur=max*maxTeam.getBid();
            flag=maxTeam.getBid()<=budget*maxCur/(2*curUit);
        }
        // 执行支付报酬计算逻辑
        for (Team_event winner:winners){
            //获取不包含该中标者的候选集合
            List<Team_event> tmpTeam=new ArrayList<>(biddderCopy);
            tmpTeam.remove(winner);
            List<Team_event> tmpWin=new ArrayList<>();
            double tmpMax=0;
            Team_event tmpMaxTeam=null;
            //执行上述计算获胜者逻辑
            for (Team_event team_event:tmpTeam){
                double comUitity=Math.log(10*team_event.getAchievementScore())/team_event.getBid();
                if (comUitity>tmpMax){
                    tmpMaxTeam=team_event;
                    tmpMax=comUitity;
                }
            }
            boolean tmpflag=tmpMaxTeam.getBid()<budget/2;
            while (tmpTeam.size()>0&&tmpflag){
                double pre=compute(tmpWin);
                tmpMax=0;
                tmpMaxTeam=null;
                double curUit=0;
                for (Team_event team_event:tmpTeam){
                    curUit=compute(tmpWin,team_event);
                    double marignUitity=(curUit-pre)/team_event.getBid();
                    if (marignUitity>tmpMax){
                        tmpMax=marignUitity;
                        tmpMaxTeam=team_event;
                    }
                }
                double curTmpMax=tmpMax*tmpMaxTeam.getBid();
                double fis=compute(tmpWin,winner)-compute(tmpWin);
                // 根据临界价格公式更新获胜者支付报酬
                salrys.put(Math.toIntExact(winner.getTeamId()),Math.max(salrys.getOrDefault(winner.getTeamId(),  0.0)
                        ,Math.min(budget*fis/(2*compute(tmpWin,winner)),
                                fis*tmpMaxTeam.getBid()/curTmpMax)));
                tmpflag=tmpMaxTeam.getBid()<=budget*curTmpMax/(2*curUit);

                tmpWin.add(tmpMaxTeam);
                tmpTeam.remove(tmpMaxTeam);
            }
        }
        // 更新结果集合
        for (Team_event winner :winners){
            winner.setState(1);
            winner.setSalary(salrys.get(winner.getTeamId()));
        }
        return winners;
    }

    // 计算企业对接科研团队的效用
    private double compute(List<Team_event> winners, Team_event team_event) {

        double totalScore=0;
        for (Team_event event:winners){
            totalScore+=event.getAchievementScore();
        }
        totalScore+=team_event.getAchievementScore();
        return 100*Math.log(10*totalScore);
    }
    // 计算企业对接科研团队的效用
    private double compute(List<Team_event> S) {
        if(S.isEmpty()){
            return 0;
        }
        double totalScore=0;
        for (Team_event event:S){
            totalScore+=event.getAchievementScore();
        }
        return  100*Math.log(10*totalScore);
    }


    //得到分数
    private double getScore(Event event, Long teamId) {
        double score = 0;
        //0为论文*0.5，1为专利*0.7，2为项目*1
        for (int i = 0; i < 3; i++) {
            //查询科研成果
            List<Achievement> achievements = achievementMapper.getAchievementByTeamIdAndType(teamId, i);
            for (Achievement achievement: achievements) {
                //通过科研成果详情计算SimHash
                SimHash hash1 = new SimHash(achievement.getDescription(), 64);
                //计算科研成果对应的SimHash和任务详情SimHash的汉明距离 即文本匹配度
                double semblance = hash1.getSemblance(new SimHash(event.getDescription(), 64));
                if (i == 0) {
                    score += semblance *0.5;
                }
                if (i == 1) {
                    score += semblance * 0.7;
                }
                if (i == 2) {
                    score += semblance ;
                }
            }

        }
        return score;
    }

    //比较大小
    private Double getMaxScore(List<Double> list){
        return Collections.max(list);

    }


    //对中标的团队发送邮件
    public void sendMail(List<String> to) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        // 构建一个邮件对象
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
        // 设置邮件主题
        helper.setSubject("恭喜您成功中标");
        // 设置邮件发送者，这个跟application.yml中设置的要一致
        helper.setFrom("15358755057@163.com");
        helper.setTo(to.toString());
        // 设置邮件发送日期
        helper.setSentDate(new Date());
        // 设置邮件的正文 true：是html文件
        helper.setText("<h1 >感谢您在此竞标中的付出和辛勤努力，祝贺您成为最终的赢家。</h1>"+"<p>(详细信息请于我的合作中查看)</p>",true);
        // 发送邮件
        javaMailSender.send(mimeMessage);
    }

}
