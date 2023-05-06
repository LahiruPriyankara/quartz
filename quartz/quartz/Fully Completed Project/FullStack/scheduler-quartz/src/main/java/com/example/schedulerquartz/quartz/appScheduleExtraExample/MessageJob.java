package com.example.schedulerquartz.quartz.appScheduleExtraExample;

import java.text.MessageFormat;
import java.util.Optional;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.schedulerquartz.quartz.entity.SchedulerJobInfoEntity;
import com.example.schedulerquartz.quartz.repository.SchedulerRepository;

public class MessageJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(MessageJob.class);

	@Autowired
	private SchedulerRepository messageRepository;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		/* Get message id recorded by scheduler during scheduling */
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String param = dataMap.getString("param");
		log.info(MessageFormat.format("(QUARTZ) Job: {0}; Param: {1}; Thread: {2}", getClass(), param,
				Thread.currentThread().getName()));

		String messageId = dataMap.getString("messageId");

		log.info("Executing job for message id {}", messageId);

		/* Get message from database by id */
		int id = Integer.parseInt(messageId);
		Optional<SchedulerJobInfoEntity> messageOpt = messageRepository.findById(id);

		/* update message visible in database */
		SchedulerJobInfoEntity message = messageOpt.get();
		message.setVisible(true);
		messageRepository.save(message);

		/* unschedule or delete after job gets executed */

		try {
			context.getScheduler().deleteJob(new JobKey(messageId));

			TriggerKey triggerKey = new TriggerKey(messageId);

			context.getScheduler().unscheduleJob(triggerKey);

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
