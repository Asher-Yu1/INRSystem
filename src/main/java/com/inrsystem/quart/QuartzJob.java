package com.inrsystem.quart;



import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import org.quartz.*;
import org.quartz.ee.jmx.jboss.QuartzService;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author: scarborough
 * @datetime: 2023/9/22 - 0:24
 */
@Slf4j
@Component
public abstract class QuartzJob implements Job {

    /**
     * 填入任务类型(必填) eg: "order_pay_end:"
     */
    public abstract String getTypeName();

    /**
     * 任务名
     */
    public String getName() {
        return "taskName";
    }

    /**
     * 任务执行时间名
     */
    public String getTimeName() {
        return "timeAt";
    }

    /**
     * 到了任务执行时间会从这里出来
     * 如需接收其他的值,请修改BaseTask内的execute(JobExecutionContext context)方法
     */
    public abstract void nextStep(Integer value);

    public Class getMessageClass() {
        return this.getClass();
    }

    @Override
    public void execute(JobExecutionContext context) {
        JobDetail detail = context.getJobDetail();
        if (detail == null) {
            return;
        }
        String value = detail.getJobDataMap().getString(getName());
        if (Strings.isBlank(value)) {
            return;
        }
        nextStep(Integer.valueOf(value));
    }

    /**
     * 加入任务(到时间一次执行无循环)
     *
     * @param value 任务参数
     * @param end   任务执行时间
     */
    public void time(String value, Instant end) {
        if (Strings.isBlank(value) || end == null) {
            throw new RuntimeException("参数不能为空");
        }

        try {
            //创建scheduler
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            long second = end.toEpochMilli();

            TriggerKey triggerKey = new TriggerKey(getTypeName() + value);

            SimpleTriggerImpl trigger = (SimpleTriggerImpl) scheduler.getTrigger(triggerKey);
            JobDetail jobDetail = scheduler.getJobDetail(new JobKey(getTypeName() + value));
            if (trigger != null && jobDetail != null) {
                trigger.setStartTime(new Date(second));
                jobDetail = jobDetail.getJobBuilder()
                        .usingJobData(getName(), value)
                        .usingJobData(getTimeName(), second)
                        .build();

                // 删除任务调度
                scheduler.deleteJob(new JobKey(getTypeName() + value));
                //加入这个调度
                scheduler.scheduleJob(jobDetail, trigger);

            } else {
                //定义一个Trigger 设置开始和结束时间   类型+值拼接 eg: order_pay_end_orderId:1
                Trigger newTrigger = newTrigger().withIdentity(getTypeName() + value)
                        .startAt(new Date(second)
                        ).build();

                //定义一个JobDetail 传入携带参数 可用于查询执行时间
                //定义Job类为HelloQuartz类，这是真正的执行逻辑所在
                JobDetail job = newJob(getMessageClass())
                        .withIdentity(getTypeName() + value)
                        .usingJobData(getName(), value)
                        .usingJobData(getTimeName(), second)
                        .build();

                //加入这个调度
                scheduler.scheduleJob(job, newTrigger);
            }
        } catch (Exception e) {
            log.info("--- 加入scheduler失败 ---", e);
        }
    }

    /**
     * 获取任务的执行时间
     */
    public Instant get(String value) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobDetail jobDetail = scheduler.getJobDetail(new JobKey(getTypeName() + value));
            if (jobDetail == null) {
                return null;
            }
            Long endAt = (Long) jobDetail.getJobDataMap().get(getTimeName());
            return Instant.ofEpochMilli(endAt);
        } catch (SchedulerException e) {
            log.info("--- 获取scheduler失败 ---", e);
            return null;
        }
    }

    /**
     * 判断任务调度是否存在
     */
    public Boolean isExist(String value) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobDetail jobDetail = scheduler.getJobDetail(new JobKey(getTypeName() + value));
            return jobDetail != null;
        } catch (SchedulerException e) {
            log.info("---  判断scheduler失败 ---", e);
            return false;
        }
    }

    /**
     * 删除任务调度
     */
    public void delete(String value) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobKey jobKey = new JobKey(getTypeName() + value);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail != null) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            log.info("--- 删除scheduler失败 ---", e);
        }
    }
}