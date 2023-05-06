package com.example.schedulerquartz.cronjob;

import java.text.MessageFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CronSchedulerTest {

	//@Scheduled(cron = "${cron-time}")
	public void everyFiveSeconds() {
		log.info(MessageFormat.format("(CRON) Periodic task.Date: {0}; Thread: {1}", new Date().toString(),
				Thread.currentThread().getName()));
	}
}
