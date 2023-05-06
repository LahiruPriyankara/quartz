package com.example.schedulerquartz.quartz.job;

import java.text.MessageFormat;
import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import lombok.extern.slf4j.Slf4j;

@DisallowConcurrentExecution
@Slf4j
public class SimpleCronJob extends QuartzJobBean {
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info(context.toString());
		log.info(MessageFormat.format("(QUARTZ) SimpleCronJob fired successfully.Date:{0}; Thread: {1}",
				new Date().toString(), Thread.currentThread().getName()));
	}
}
