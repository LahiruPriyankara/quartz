package com.example.schedulerquartz.quartz.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SchedulerJobInfoRequestDto {
	
	@JsonProperty("isCronJob")
	private boolean isCronJob;

	@NotNull
	private String jobName;
	@NotNull
	private String description;
	
	private String cronExpression;
	
	private long repeatTime;
}