package com.example.schedulerquartz.quartz.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private QuartzProperties quartzProperties;

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() throws IOException {

		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();

		schedulerFactory.setOverwriteExistingJobs(true);
		schedulerFactory.setDataSource(dataSource);
		schedulerFactory.setQuartzProperties(quartzProperties());
		schedulerFactory.setJobFactory(jobFactory());
		schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
		schedulerFactory.setAutoStartup(true);

		return schedulerFactory;
	}

	@Bean
	public JobFactory jobFactory() {
		SchedulerJobFactory jobFactory = new SchedulerJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	public Properties quartzProperties() throws IOException {

		Properties properties = new Properties();
		properties.putAll(quartzProperties.getProperties());
		return properties;
//          PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//          propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
//          propertiesFactoryBean.afterPropertiesSet();
//          return propertiesFactoryBean.getObject();
	}
}