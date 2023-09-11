package com.inrsystem;

import com.inrsystem.util.AddressChangeUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class InrSystemApplication {

	public static void main(String[] args) {

		SpringApplication.run(InrSystemApplication.class, args);

	}

}
