package com.rajat.taskmanager_spring_java.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*
* author : Rajat
*
//To connect docker container "postgres-db" and directly access my database. below is command
//docker exec -it postgres-db psql -U postgres -d taskmanager
* for quit \q
* alter table if exists public.tasks alter column id set data type varchar(255)
* */
@SpringBootApplication
@ComponentScan(basePackages = "com.rajat.taskmanager_spring_java.taskmanager")
@EnableJpaRepositories(basePackages = "com.rajat.taskmanager_spring_java.taskmanager.repository")
public class TaskmanagerApplication {
	public static void main(String[] args) {
		SpringApplication.run(TaskmanagerApplication.class, args);
	}
}