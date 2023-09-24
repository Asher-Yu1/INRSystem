package com.inrsystem;

import com.inrsystem.quart.SelectWinner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@EnableScheduling
@SpringBootApplication
public class InrSystemApplication {
	public static void main(String[] args) {

		SpringApplication.run(InrSystemApplication.class, args);
		SelectWinner selectWinner =new SelectWinner();
		selectWinner.time("1", Instant.now().plus(10, ChronoUnit.SECONDS));
	}


}
