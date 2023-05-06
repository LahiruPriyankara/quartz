package com.example.schedulerquartz.quartz.appThymeleafExample;

import java.util.Date;

import lombok.Data;

@Data
public class User {
	private String name;
	private String email;
	private String password;
	private String gender;
	private String note;
	private boolean married;
	private Date birthday;
	private String profession;
}
