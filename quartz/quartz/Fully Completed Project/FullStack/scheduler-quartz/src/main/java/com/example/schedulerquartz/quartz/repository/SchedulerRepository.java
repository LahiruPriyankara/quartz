package com.example.schedulerquartz.quartz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.schedulerquartz.quartz.entity.SchedulerJobInfoEntity;

@Repository
public interface SchedulerRepository extends JpaRepository<SchedulerJobInfoEntity, Integer> {
	SchedulerJobInfoEntity findByJobName(String jobName);
}
