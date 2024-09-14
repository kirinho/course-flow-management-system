package com.liushukov.courseFlow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CourseFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseFlowApplication.class, args);
	}

}
