package com.example.schedulerquartz.quartz.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.schedulerquartz.quartz.config.QuartzConfig;
import com.example.schedulerquartz.quartz.dto.MessageDto;
import com.example.schedulerquartz.quartz.entity.SchedulerJobInfoEntity;
import com.example.schedulerquartz.quartz.job.MessageJob;
import com.example.schedulerquartz.quartz.repository.SchedulerRepository;

@RestController
@RequestMapping(path = "/messages")
public class QuartzSchedulingController {
	@Autowired
	private QuartzConfig quartzConfig;
	@Autowired
	private SchedulerRepository messageRepository;

	@PostMapping(path = "/schedule-visibility")
	public @ResponseBody MessageDto scheduleMessageVisibility(@RequestBody MessageDto messageDto) {
		try {
			// save messages in table
			SchedulerJobInfoEntity message = new SchedulerJobInfoEntity();
			message.setContent(messageDto.getContent());
			message.setVisible(false);
			message.setMakeVisibleAt(messageDto.getMakeVisibleAt());

			message = messageRepository.save(message);

			// Creating JobDetail instance
			String id = String.valueOf(message.getId());
			JobDetail jobDetail = JobBuilder.newJob(MessageJob.class).withIdentity(id).build();

			// Adding JobDataMap to jobDetail
			jobDetail.getJobDataMap().put("messageId", id);

			// Scheduling time to run job
			Date triggerJobAt = new Date(message.getMakeVisibleAt());

			SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity(id).startAt(triggerJobAt)
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
					.build();
			// Getting scheduler instance
			Scheduler scheduler = quartzConfig.schedulerFactoryBean().getScheduler();
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();

			messageDto.setStatus("SUCCESS");

		} catch (IOException | SchedulerException e) {
			// scheduling failed
			messageDto.setStatus("FAILED");
			e.printStackTrace();
		}
		return messageDto;
	}

	@DeleteMapping(path = "/{messageId}/unschedule-visibility")
	public @ResponseBody MessageDto unscheduleMessageVisibility(@PathVariable(name = "messageId") Integer messageId) {

		MessageDto messageDto = new MessageDto();

		Optional<SchedulerJobInfoEntity> messageOpt = messageRepository.findById(messageId);
		if (!messageOpt.isPresent()) {
			messageDto.setStatus("Message Not Found");
			return messageDto;
		}

		SchedulerJobInfoEntity message = messageOpt.get();
		message.setVisible(false);
		messageRepository.save(message);

		String id = String.valueOf(message.getId());

		try {
			Scheduler scheduler = quartzConfig.schedulerFactoryBean().getScheduler();

			scheduler.deleteJob(new JobKey(id));
			TriggerKey triggerKey = new TriggerKey(id);
			scheduler.unscheduleJob(triggerKey);
			messageDto.setStatus("SUCCESS");

		} catch (IOException | SchedulerException e) {
			messageDto.setStatus("FAILED");
			e.printStackTrace();
		}
		return messageDto;
	}
}
