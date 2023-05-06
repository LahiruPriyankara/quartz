package com.example.schedulerquartz.quartz.job;

import java.text.MessageFormat;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleJob extends QuartzJobBean {
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info(context.toString());
		log.info(MessageFormat.format("(QUARTZ) SimpleJob fired successfully.Date:{0}; Thread: {1}",
				new Date().toString(), Thread.currentThread().getName()));
	}
}
