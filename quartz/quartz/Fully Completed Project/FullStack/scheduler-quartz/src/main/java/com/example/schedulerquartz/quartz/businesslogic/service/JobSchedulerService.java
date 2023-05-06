package com.example.schedulerquartz.quartz.businesslogic.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.schedulerquartz.quartz.businesslogic.quartzhelper.JobScheduleCreator;
import com.example.schedulerquartz.quartz.dto.request.SchedulerJobInfoRequestDto;
import com.example.schedulerquartz.quartz.dto.response.SchedulerJobInfoResponseDto;
import com.example.schedulerquartz.quartz.entity.SchedulerJobInfoEntity;
import com.example.schedulerquartz.quartz.job.SimpleCronJob;
import com.example.schedulerquartz.quartz.job.SimpleJob;
import com.example.schedulerquartz.quartz.repository.SchedulerRepository;
import com.example.schedulerquartz.quartz.utils.JobGroupEnum;
import com.example.schedulerquartz.quartz.utils.JobStatusEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@AllArgsConstructor
@Slf4j
public class JobSchedulerService {

	private ApplicationContext appContext;// From spring container.
	private SchedulerFactoryBean schedulerFactoryBean;// Bean has been created in configuration class.
	private JobScheduleCreator scheduleCreator;// Bean has been created in component package.
	private SchedulerRepository schedulerRepository;

	public SchedulerJobInfoResponseDto saveOrUpdate(SchedulerJobInfoRequestDto request) {

		SchedulerJobInfoEntity scheduleJobEntity = schedulerRepository.findByJobName(request.getJobName());

		if (Objects.isNull(scheduleJobEntity)) {
			scheduleJobEntity = new SchedulerJobInfoEntity();
		}

		String className, jobGroup;
		if (Boolean.TRUE.equals(request.isCronJob())) {
			if (Objects.isNull(request.getCronExpression())) {
				return new SchedulerJobInfoResponseDto("FAILED", "CronExpression is required.");
			}
			scheduleJobEntity.setCronExpression(request.getCronExpression());
			className = SimpleCronJob.class.getName();
			jobGroup = JobGroupEnum.CRONJOB.name();

		} else {
			if (request.getRepeatTime() < 0) {
				return new SchedulerJobInfoResponseDto("FAILED", "RepeatTime should be greater than zero.");
			}
			scheduleJobEntity.setRepeatTime(request.getRepeatTime());
			className = SimpleJob.class.getName();
			jobGroup = JobGroupEnum.SIMPLETRIGGER.name();
		}

		scheduleJobEntity.setJobClass(className);
		scheduleJobEntity.setIsCronJob(request.isCronJob());
		scheduleJobEntity.setJobName(request.getJobName());
		scheduleJobEntity.setDescription(request.getDescription());
		scheduleJobEntity.setJobGroup(jobGroup);

		if (Objects.isNull(scheduleJobEntity.getId())) {
			return scheduleNewJob(scheduleJobEntity);
		} else {
			return updateScheduleJob(scheduleJobEntity);
		}
	}

	@SuppressWarnings("unchecked")
	private SchedulerJobInfoResponseDto scheduleNewJob(SchedulerJobInfoEntity jobInfoEntity) {
		String response = null;
		String code = "FAILED";
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();

			JobDetail jobDetail = JobBuilder
					.newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfoEntity.getJobClass()))
					.withIdentity(jobInfoEntity.getJobName(), jobInfoEntity.getJobGroup()).build();

			if (!scheduler.checkExists(jobDetail.getKey())) {

				jobDetail = scheduleCreator.createJob(
						(Class<? extends QuartzJobBean>) Class.forName(jobInfoEntity.getJobClass()), false, appContext,
						jobInfoEntity.getJobName(), jobInfoEntity.getJobGroup());

				Trigger trigger;
				if (Boolean.TRUE.equals(jobInfoEntity.getIsCronJob())) {
					trigger = scheduleCreator.createCronTrigger(jobInfoEntity.getJobName(), new Date(),
							jobInfoEntity.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
				} else {
					trigger = scheduleCreator.createSimpleTrigger(jobInfoEntity.getJobName(), new Date(),
							jobInfoEntity.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
				}
				scheduler.scheduleJob(jobDetail, trigger);

				jobInfoEntity.setJobStatus(JobStatusEnum.CREATED_SCHEDULED);
				schedulerRepository.save(jobInfoEntity);

				response = "Successfully Created Scheduled the JOB.JobName = [" + jobInfoEntity.getJobName() + "]";
				code = "OK";
			} else {
				response = "scheduleNewJobRequest.jobAlreadyExist";
			}
		} catch (ClassNotFoundException e) {
			response = "Class Not Found - " + jobInfoEntity.getJobClass() + ".Exception:" + e;
		} catch (SchedulerException e) {
			response = "Failed to Created Scheduled the job - " + jobInfoEntity.getJobName() + ".Exception:" + e;
		}
		return new SchedulerJobInfoResponseDto(code, response);
	}

	private SchedulerJobInfoResponseDto updateScheduleJob(SchedulerJobInfoEntity jobInfoEntity) {
		String response = null;
		String code = "FAILED";
		Trigger newTrigger;
		try {
			if (Boolean.TRUE.equals(jobInfoEntity.getIsCronJob())) {

				newTrigger = scheduleCreator.createCronTrigger(jobInfoEntity.getJobName(), new Date(),
						jobInfoEntity.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
			} else {

				newTrigger = scheduleCreator.createSimpleTrigger(jobInfoEntity.getJobName(), new Date(),
						jobInfoEntity.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
			}

			schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfoEntity.getJobName()),
					newTrigger);

			jobInfoEntity.setJobStatus(JobStatusEnum.EDITED_SCHEDULED);
			schedulerRepository.save(jobInfoEntity);

			response = "Successfully Updated and Scheduled the JOB.JobName = [" + jobInfoEntity.getJobName() + "]";
			code = "OK";
		} catch (SchedulerException e) {
			response = "Failed to Updated and Scheduled the job - " + jobInfoEntity.getJobName() + ".Exception:" + e;
		}
		return new SchedulerJobInfoResponseDto(code, response);
	}

	public SchedulerJobInfoResponseDto startJobNow(String jobName) {
		String response = null;
		String code = "FAILED";
		try {
			SchedulerJobInfoEntity jobInfoEntity = getSchedulerJobInfoEntityByName(jobName);

			jobInfoEntity.setJobStatus(JobStatusEnum.JOB_STARTED);
			schedulerRepository.save(jobInfoEntity);

			schedulerFactoryBean.getScheduler()
					.triggerJob(new JobKey(jobInfoEntity.getJobName(), jobInfoEntity.getJobGroup()));

			response = "Successfully Scheduled  and Started the JOB.JobName = [" + jobInfoEntity.getJobName() + "]";
			code = "OK";
		} catch (Exception e) {
			response = "Failed to start new job - " + jobName + ".Exception:" + e;
		}
		return new SchedulerJobInfoResponseDto(code, response);
	}

	public SchedulerJobInfoResponseDto pauseJob(String jobName) {
		String response = null;
		String code = "FAILED";
		try {
			SchedulerJobInfoEntity jobInfoEntity = getSchedulerJobInfoEntityByName(jobName);

			jobInfoEntity.setJobStatus(JobStatusEnum.JOB_PAUSED);
			schedulerRepository.save(jobInfoEntity);

			schedulerFactoryBean.getScheduler()
					.pauseJob(new JobKey(jobInfoEntity.getJobName(), jobInfoEntity.getJobGroup()));

			response = "Successfully Paused the JOB.JobName = [" + jobInfoEntity.getJobName() + "]";
			code = "OK";
		} catch (Exception e) {
			response = "Failed to pause job - " + jobName + ".Exception:" + e;
		}
		return new SchedulerJobInfoResponseDto(code, response);
	}

	public SchedulerJobInfoResponseDto resumeJob(String jobName) {
		String response = null;
		String code = "FAILED";
		try {
			SchedulerJobInfoEntity jobInfoEntity = getSchedulerJobInfoEntityByName(jobName);

			jobInfoEntity.setJobStatus(JobStatusEnum.JOB_RESUMED);
			schedulerRepository.save(jobInfoEntity);

			schedulerFactoryBean.getScheduler()
					.resumeJob(new JobKey(jobInfoEntity.getJobName(), jobInfoEntity.getJobGroup()));

			response = "Successfully Resumed the JOB.JobName = [" + jobInfoEntity.getJobName() + "]";
			code = "OK";
		} catch (Exception e) {
			response = "Failed to resume job - " + jobName + ".Exception:" + e;
		}
		return new SchedulerJobInfoResponseDto(code, response);
	}

	public SchedulerJobInfoResponseDto deleteJob(String jobName) {
		String response = null;
		String code = "FAILED";
		try {
			SchedulerJobInfoEntity jobInfoEntity = getSchedulerJobInfoEntityByName(jobName);

			schedulerRepository.delete(jobInfoEntity);

			schedulerFactoryBean.getScheduler()
					.deleteJob(new JobKey(jobInfoEntity.getJobName(), jobInfoEntity.getJobGroup()));

			response = "Successfully Deleted the JOB.JobName = [" + jobInfoEntity.getJobName() + "]";
			code = "OK";
		} catch (Exception e) {
			response = "Failed to delete job - " + jobName + ".Exception:" + e;
		}
		return new SchedulerJobInfoResponseDto(code, response);
	}

	private SchedulerJobInfoEntity getSchedulerJobInfoEntityByName(String jobName) throws Exception {
		SchedulerJobInfoEntity jobInfo = schedulerRepository.findByJobName(jobName);
		if (Objects.isNull(jobInfo)) {
			throw new Exception("Invalid JobName:" + jobName);
		}
		return jobInfo;
	}

	public List<SchedulerJobInfoEntity> getAllJobList() {

		List<SchedulerJobInfoEntity> infoEntities = schedulerRepository.findAll();
		infoEntities = infoEntities.stream()
				.sorted(Comparator.comparing(SchedulerJobInfoEntity::getJobStatus))
				.collect(Collectors.toList());

		return infoEntities;
	}

	public SchedulerJobInfoRequestDto getJobByName(String jobname) {

		SchedulerJobInfoEntity jobInfo = schedulerRepository.findByJobName(jobname);
		log.info("jobInfo :" + jobInfo);

		SchedulerJobInfoRequestDto infoRequestDto = new SchedulerJobInfoRequestDto();

		if (Objects.nonNull(jobInfo)) {
			infoRequestDto.setJobName(jobInfo.getJobName());
			infoRequestDto.setJobGroup(jobInfo.getJobGroup());
			infoRequestDto.setDescription(jobInfo.getDescription());
			infoRequestDto.setCronJob(jobInfo.getIsCronJob());

			String cronJobStr = "0";
			if (Objects.nonNull(jobInfo.getIsCronJob()) && Boolean.TRUE.equals(jobInfo.getIsCronJob())) {
				cronJobStr = "1";
			}
			infoRequestDto.setCronJobStr(cronJobStr);

			infoRequestDto.setCronExpression(jobInfo.getCronExpression());
			infoRequestDto.setRepeatTime(jobInfo.getRepeatTime());
		}

		log.info("infoRequestDto :" + infoRequestDto);

		return infoRequestDto;
	}

}
/*SLOD
*
* S- Single responsibility principle
* Eka deyak witarak kranna one
*
* L - L
*
* O - Open close principle
* Parent cals change kranne naaa..child cla witarai wesn kranne.Open for extend close for modification
*
* D -
* */