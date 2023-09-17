package com.inrsystem;

import com.inrsystem.dao.Event;
import com.inrsystem.mapper.EventMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;

@SpringBootTest
@EnableScheduling
class InrSystemApplicationTests {
	@Resource
	private EventMapper eventMapper;
	@Test
	void contextLoads() {
	}


}
