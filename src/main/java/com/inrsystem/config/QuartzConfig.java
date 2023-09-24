package com.inrsystem.config;


import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;



@Configuration
@Slf4j
@Order(99)
public class QuartzConfig implements CommandLineRunner {

    public void init() {
        schedulerStart();
    }

    @Override
    public void run(String... args) {
        init();
    }

    public void schedulerStart() {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            log.info("--- 启动Scheduler失败 ---", e);
        }
    }
}
