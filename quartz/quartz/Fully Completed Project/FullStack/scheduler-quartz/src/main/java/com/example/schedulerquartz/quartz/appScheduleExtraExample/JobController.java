package com.example.schedulerquartz.quartz.appScheduleExtraExample;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.schedulerquartz.quartz.businesslogic.service.JobSchedulerService;
import com.example.schedulerquartz.quartz.dto.request.SchedulerJobInfoRequestDto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("ui/job")
@AllArgsConstructor
public class JobController {

	private final JobSchedulerService scheduleJobService;

	/*
	 * @RequestMapping(value = "/index/string", method = RequestMethod.GET) public
	 * String index() { log.info("JobController.index");
	 * 
	 * return "index"; }
	 * 
	 * @RequestMapping(value = "index/modelmap", method = RequestMethod.GET) public
	 * String modelmapTest(ModelMap map) { log.info("JobController.modelmapTest");
	 * 
	 * map.addAttribute("welcomeMessage", "welcome"); map.addAttribute("message",
	 * "Baeldung");
	 * 
	 * return "index"; }
	 * 
	 * @RequestMapping(value = "index/modelAndView", method = RequestMethod.GET)
	 * public ModelAndView modelAnddViewTest() {
	 * log.info("JobController.modelAnddViewTest");
	 * 
	 * ModelAndView modelAndView = new ModelAndView("index");
	 * modelAndView.addObject("jobs", getAllJobs());
	 * 
	 * return modelAndView; }
	 */

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public ModelAndView getAllJobs() {
		log.info("JobController.getAllJobs");

		ModelAndView modelAndView = new ModelAndView("index");
		modelAndView.addObject("jobs", scheduleJobService.getAllJobList());
		modelAndView.addObject("schedulerJobInfo", new SchedulerJobInfoRequestDto());

		java.util.List<String> jobGroup = Arrays.asList("CRONJOB", "SIMPLETRIGGER");
		modelAndView.addObject("jobGroups", jobGroup);

		modelAndView.addObject("jobs", scheduleJobService.getAllJobList());

		return modelAndView;
	}

	@RequestMapping(value = "details/byname/{jobname}", method = RequestMethod.POST)
	public ModelAndView getJobByName(@PathVariable(name = "jobname") String jobName) {
		log.info("JobController.getJobByName : " + jobName);

		ModelAndView modelAndView = new ModelAndView("include/form");
		modelAndView.addObject("schedulerJobInfo", scheduleJobService.getJobByName(jobName));

		List<String> jobGroup = Arrays.asList("CRONJOB", "SIMPLETRIGGER");
		modelAndView.addObject("jobGroups", jobGroup);

		return modelAndView;
	}

	@PostMapping("create_update")
	public ModelAndView createUpdate(
			@ModelAttribute("schedulerJobInfo") @Validated SchedulerJobInfoRequestDto request) {
		log.info("JobController.createUpdate : " + request);

		request.setCronJob(request.getCronJobStr().equals("1") ? true : false);

		scheduleJobService.saveOrUpdate(request);

		log.info("Successfully Created...");
		
		return new ModelAndView("redirect:/ui/job/index");

	}

	@PostMapping("start/byname/{jobname}")
	public ModelAndView startJobNow(@PathVariable(name = "jobname") String jobName) {

		log.info("JobController.startJobNow : " + jobName);
		scheduleJobService.startJobNow(jobName);

		// return new ModelAndView("redirect:/ui/job/redirectWithJobsStatusUpdate");

		ModelAndView modelAndView = new ModelAndView("include/jobs-table");
		modelAndView.addObject("jobs", scheduleJobService.getAllJobList());

		log.info("Successfully Started...");

		return modelAndView;
	}

	@PostMapping("pause/byname/{jobname}")
	public ModelAndView pauseJob(@PathVariable(name = "jobname") String jobName) {

		log.info("JobController.pauseJob : " + jobName);
		scheduleJobService.pauseJob(jobName);

		ModelAndView modelAndView = new ModelAndView("include/jobs-table");
		modelAndView.addObject("jobs", scheduleJobService.getAllJobList());

		log.info("Successfully Paused...");

		return modelAndView;
	}

	@PostMapping("resume/byname/{jobname}")
	public ModelAndView resumeJob(@PathVariable(name = "jobname") String jobName) {

		log.info("JobController.resumeJob : " + jobName);
		scheduleJobService.resumeJob(jobName);

		ModelAndView modelAndView = new ModelAndView("include/jobs-table");
		modelAndView.addObject("jobs", scheduleJobService.getAllJobList());

		log.info("Successfully Resumed...");

		return modelAndView;
	}

	@PostMapping("delete/byname/{jobname}")
	public ModelAndView deleteJob(@PathVariable(name = "jobname") String jobName) {

		log.info("JobController.deleteJob : " + jobName);
		scheduleJobService.deleteJob(jobName);

		ModelAndView modelAndView = new ModelAndView("include/jobs-table");
		modelAndView.addObject("jobs", scheduleJobService.getAllJobList());

		log.info("Successfully Deleted...");

		return modelAndView;
	}

	@GetMapping("redirectWithJobsStatusUpdate")
	public ModelAndView redirectWithJobsStatusUpdate() {
		log.info("JobController.redirectWithJobsStatusUpdate");

		ModelAndView modelAndView = new ModelAndView("include/jobs-table");
		modelAndView.addObject("jobs", scheduleJobService.getAllJobList());

		log.info("Successfully Deleted...");

		return modelAndView;
	}

}