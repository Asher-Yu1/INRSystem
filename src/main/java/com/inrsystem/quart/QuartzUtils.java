package com.inrsystem.quart;

import com.inrsystem.exception.LocalRunTimeException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DateBuilder.IntervalUnit;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


import java.util.*;
@Slf4j
@Component
public class QuartzUtils {

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    public void startScheduler() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加一个job
     *
     * @param jobClass
     *            任务实现类
     * @param jobName
     *            任务名称
     * @param jobGroupName
     *            任务组名
     * @param jobTime
     *            时间表达式 (这是每隔多少秒为一次任务)
     * @param jobTimes
     *            运行的次数 （<0:表示不限次数）
     * @param jobData
     *            参数
     */
    public void addJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, int jobTime,
                       int jobTimes, Map jobData) {
        try {
            // 任务名称和组构成任务key
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName)
                    .build();
            // 设置job参数
            if(jobData!= null && jobData.size()>0){
                jobDetail.getJobDataMap().putAll(jobData);
            }
            // 使用simpleTrigger规则
            Trigger trigger = null;
            if (jobTimes < 0) {
                trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
                        .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(1).withIntervalInSeconds(jobTime))
                        .startNow().build();
            } else {
                trigger = TriggerBuilder
                        .newTrigger().withIdentity(jobName, jobGroupName).withSchedule(SimpleScheduleBuilder
                                .repeatSecondlyForever(1).withIntervalInSeconds(jobTime).withRepeatCount(jobTimes))
                        .startNow().build();
            }
            log.info("jobDataMap: {}", jobDetail.getJobDataMap().getWrappedMap());
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new LocalRunTimeException("add job error!");
        }
    }

    /**
     * 增加一个job
     *
     * @param jobClass
     *            任务实现类
     * @param jobName
     *            任务名称(建议唯一)
     * @param jobGroupName
     *            任务组名
     * @param jobTime
     *            时间表达式 （如：0/5 * * * * ? ）
     * @param jobData
     *            参数
     */
    public void addJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, String jobTime, Map jobData) {
        try {
            // 创建jobDetail实例，绑定Job实现类
            // 指明job的名称，所在组的名称，以及绑定job类
            // 任务名称和组构成任务key
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName)
                    .build();
            // 设置job参数
            if(jobData!= null && jobData.size()>0){
                jobDetail.getJobDataMap().putAll(jobData);
            }
            // 定义调度触发规则
            // 使用cornTrigger规则
            // 触发器key
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
                    .startAt(DateBuilder.futureDate(1, IntervalUnit.SECOND))
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobTime)).startNow().build();
            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("jobDataMap: {}", jobDetail.getJobDataMap());
        } catch (Exception e) {
            e.printStackTrace();
            throw new LocalRunTimeException("add job error!");
        }
    }

    /**
     * 修改 一个job的 时间表达式
     *
     * @param jobName
     * @param jobGroupName
     * @param jobTime
     */
    public void updateJob(String jobName, String jobGroupName, String jobTime) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            log.info("new jobTime: {}", jobTime);
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobTime)).build();
            // 重启触发器
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new LocalRunTimeException("update job error!");
        }
    }

    /**
     * 删除任务一个job
     *
     * @param jobName
     *            任务名称
     * @param jobGroupName
     *            任务组名
     */
    public void deleteJob(String jobName, String jobGroupName) {
        try {
            scheduler.deleteJob(new JobKey(jobName, jobGroupName));
        } catch (Exception e) {
            e.printStackTrace();
            throw new LocalRunTimeException("delete job error!");
        }
    }

    /**
     * 暂停一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    public void pauseJob(String jobName, String jobGroupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new LocalRunTimeException("pause job error!");
        }
    }

    /**
     * 恢复一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    public void resumeJob(String jobName, String jobGroupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new LocalRunTimeException("resume job error!");
        }
    }

    /**
     * 立即执行一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    public void runAJobNow(String jobName, String jobGroupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new LocalRunTimeException("run a job error!");
        }
    }

    /**
     * 获取所有计划中的任务列表
     *
     * @return
     */
    public List<Map<String, Object>> queryAllJob() {
        List<Map<String, Object>> jobList = null;
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            jobList = new ArrayList<Map<String, Object>>();
            for (JobKey jobKey : jobKeys) {
                log.info("maps: {}", scheduler.getJobDetail(jobKey).getJobDataMap().getWrappedMap());
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("jobName", jobKey.getName());
                    map.put("jobGroupName", jobKey.getGroup());
                    map.put("description", "触发器:" + trigger.getKey());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    map.put("jobStatus", triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        map.put("jobTime", cronExpression);
                    }
                    jobList.add(map);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new LocalRunTimeException("query all jobs error!");
        }
        return jobList;
    }

    /**
     * 获取所有正在运行的job
     *
     * @return
     */
    public List<Map<String, Object>> queryRunJob() {
        List<Map<String, Object>> jobList = null;
        try {
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            jobList = new ArrayList<Map<String, Object>>(executingJobs.size());
            for (JobExecutionContext executingJob : executingJobs) {
                Map<String, Object> map = new HashMap<String, Object>();
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                map.put("jobName", jobKey.getName());
                map.put("jobGroupName", jobKey.getGroup());
                map.put("description", "触发器:" + trigger.getKey());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                map.put("jobStatus", triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    map.put("jobTime", cronExpression);
                }
                jobList.add(map);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new LocalRunTimeException("query run jobs error!");
        }
        return jobList;
    }







//    /**
//     * 创建定时任务 定时任务创建之后默认启动状态
//     * @param scheduler   调度器
//     * @param quartzBean  定时任务信息类
//     */
//    public static void createScheduleJob(Scheduler scheduler, QuartzBean quartzBean){
//        try {
//            //获取到定时任务的执行类  必须是类的绝对路径名称
//            //定时任务类需要是job类的具体实现 QuartzJobBean是job的抽象类。
//            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(quartzBean.getJobClass());
//            // 构建定时任务信息
//            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(quartzBean.getJobName()).build();
//            // 设置定时任务执行方式
//            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzBean.getCronExpression());
//            // 构建触发器trigger
//            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(quartzBean.getJobName()).withSchedule(scheduleBuilder).build();
//            scheduler.scheduleJob(jobDetail, trigger);
//        } catch (ClassNotFoundException e) {
//            System.out.println("定时任务类路径出错：请输入类的绝对路径");
//        } catch (SchedulerException e) {
//            System.out.println("创建定时任务出错："+e.getMessage());
//        }
//    }
//
//    /**
//     * 根据任务名称暂停定时任务
//     * @param scheduler  调度器
//     * @param jobName    定时任务名称
//     */
//    public static void pauseScheduleJob(Scheduler scheduler, String jobName){
//        JobKey jobKey = JobKey.jobKey(jobName);
//        try {
//            scheduler.pauseJob(jobKey);
//        } catch (SchedulerException e) {
//            System.out.println("暂停定时任务出错："+e.getMessage());
//        }
//    }
//
//    /**
//     * 根据任务名称恢复定时任务
//     * @param scheduler  调度器
//     * @param jobName    定时任务名称
//     */
//    public static void resumeScheduleJob(Scheduler scheduler, String jobName) {
//        JobKey jobKey = JobKey.jobKey(jobName);
//        try {
//            scheduler.resumeJob(jobKey);
//        } catch (SchedulerException e) {
//            System.out.println("启动定时任务出错："+e.getMessage());
//        }
//    }
//
//    /**
//     * 根据任务名称立即运行一次定时任务
//     * @param scheduler     调度器
//     * @param jobName       定时任务名称
//     */
//    public static void runOnce(Scheduler scheduler, String jobName){
//        JobKey jobKey = JobKey.jobKey(jobName);
//        try {
//            scheduler.triggerJob(jobKey);
//        } catch (SchedulerException e) {
//            System.out.println("运行定时任务出错："+e.getMessage());
//        }
//    }
//
//    /**
//     * 更新定时任务
//     * @param scheduler   调度器
//     * @param quartzBean  定时任务信息类
//     */
//    public static void updateScheduleJob(Scheduler scheduler, QuartzBean quartzBean)  {
//        try {
//            //获取到对应任务的触发器
//            TriggerKey triggerKey = TriggerKey.triggerKey(quartzBean.getJobName());
//            //设置定时任务执行方式
//            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzBean.getCronExpression());
//            //重新构建任务的触发器trigger
//            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
//            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
//            //重置对应的job
//            scheduler.rescheduleJob(triggerKey, trigger);
//        } catch (SchedulerException e) {
//            System.out.println("更新定时任务出错："+e.getMessage());
//        }
//    }
//
//    /**
//     * 根据定时任务名称从调度器当中删除定时任务
//     * @param scheduler 调度器
//     * @param jobName   定时任务名称
//     */
//    public static void deleteScheduleJob(Scheduler scheduler, String jobName) {
//        JobKey jobKey = JobKey.jobKey(jobName);
//        try {
//            scheduler.deleteJob(jobKey);
//        } catch (SchedulerException e) {
//            System.out.println("删除定时任务出错："+e.getMessage());
//        }
//    }
}

