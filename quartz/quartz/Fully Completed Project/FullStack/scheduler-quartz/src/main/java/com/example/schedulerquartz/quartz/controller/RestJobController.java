package com.example.schedulerquartz.quartz.controller;

import com.example.schedulerquartz.quartz.businesslogic.service.SchedulerJobService;
import com.example.schedulerquartz.quartz.dto.request.SchedulerJobInfoRequestDto;
import com.example.schedulerquartz.quartz.dto.response.SchedulerJobInfoResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("job")
@AllArgsConstructor
public class RestJobController {

    private final SchedulerJobService scheduleJobService;

    @PostMapping("create")
    public ResponseEntity<SchedulerJobInfoResponseDto> create(@RequestBody @Valid SchedulerJobInfoRequestDto request) {
        log.info("create, request = {}", request);
        return ResponseEntity.ok(scheduleJobService.saveOrUpdate(request));
    }

    @PostMapping("update")
    public ResponseEntity<SchedulerJobInfoResponseDto> update(@RequestBody @Valid SchedulerJobInfoRequestDto request) {
        log.info("update, request = {}", request);
        return ResponseEntity.ok(scheduleJobService.saveOrUpdate(request));
    }

    @PostMapping("start")
    public ResponseEntity<SchedulerJobInfoResponseDto> startJobNow(@RequestParam(defaultValue = "") String jobName) {
        return ResponseEntity.ok(scheduleJobService.startJobNow(jobName));
    }

    @PostMapping("pause")
    public ResponseEntity<SchedulerJobInfoResponseDto> pauseJob(@RequestParam(defaultValue = "") String jobName) {
        return ResponseEntity.ok(scheduleJobService.pauseJob(jobName));
    }

    @PostMapping("resume")
    public ResponseEntity<SchedulerJobInfoResponseDto> resumeJob(@RequestParam(defaultValue = "") String jobName) {
        return ResponseEntity.ok(scheduleJobService.resumeJob(jobName));
    }

    @PostMapping("delete")
    public ResponseEntity<SchedulerJobInfoResponseDto> deleteJob(@RequestParam(defaultValue = "") String jobName) {
        return ResponseEntity.ok(scheduleJobService.deleteJob(jobName));
    }

    @GetMapping("getAllJobs")
    public Object getAllJobs() {
        return scheduleJobService.getAllJobList();
    }
}