package com.example.schedulerquartz.quartz.appScheduleExtraExample;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageDto {
	private String content;
	private Long makeVisibleAt;
	private LocalDateTime created;
	private LocalDateTime modified;
	private String status;
}
