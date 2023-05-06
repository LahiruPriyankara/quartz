package com.example.schedulerquartz.quartz.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedulerJobInfoRequestDto {

    @JsonProperty("isCronJob")
    private boolean isCronJob;

    //Only for Thymeleaf
    private String cronJobStr;

    @NotNull
    private String jobName;
    @NotNull
    private String description;

    private String cronExpression;

    private Long repeatTime;

    private String jobGroup;
}