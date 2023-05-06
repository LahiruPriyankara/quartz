package com.example.schedulerquartz.quartz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class SchedulerJobInfoResponseDto {
	private String code;
	private String response;
}
