package com.example.schedulerquartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchedulerQuartzApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerQuartzApplication.class, args);
	}

}
