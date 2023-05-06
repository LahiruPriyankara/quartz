package com.example.schedulerquartz.quartz.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.schedulerquartz.quartz.businesslogic.service.SchedulerJobService;
import com.example.schedulerquartz.quartz.dto.request.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("user/test-thymeleaf")
@AllArgsConstructor
public class UserController {

	private final SchedulerJobService scheduleJobService;

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

	@RequestMapping(value = "user", method = RequestMethod.GET)
	public ModelAndView getUser() {
		log.info("UserController.getUser");

		ModelAndView modelAndView = new ModelAndView("user");
			
		User user = new User();
		modelAndView.addObject("user", user);
         
        List<String> listProfession = Arrays.asList("Developer", "Tester", "Architect");
        modelAndView.addObject("listProfession", listProfession);


		return modelAndView;
	}
	
	@PostMapping("/register")
	public ModelAndView submitForm(@ModelAttribute("user") User user) {
		log.info("UserController.submitForm:"+user);
	    return getUser();
	}

}