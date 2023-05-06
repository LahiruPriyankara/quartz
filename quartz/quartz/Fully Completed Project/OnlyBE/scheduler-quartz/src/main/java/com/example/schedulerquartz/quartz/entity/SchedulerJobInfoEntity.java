package com.example.schedulerquartz.quartz.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.schedulerquartz.quartz.utils.JobStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SchedulerJobInfo")
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedulerJobInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String jobName;
	private String description;
	private String jobGroup;

	@Enumerated(EnumType.STRING)
	private JobStatusEnum jobStatus;// SCHEDULED, EDITED_SCHEDULED, SCHEDULED_STARTED, PAUSED, RESUMED

	private String jobClass;// SimpleCronJob or SimpleJob
	private String cronExpression;// 10 * * * * ?
	private String interfaceName;
	private Long repeatTime;
	private Boolean isCronJob;

	@CreationTimestamp
	private LocalDateTime createdDateTime;
	@UpdateTimestamp
	private LocalDateTime modifiedDateTime;

	// Those are currently not necessary
	private String content;
	private Boolean visible = false;
	private Long makeVisibleAt;
}
