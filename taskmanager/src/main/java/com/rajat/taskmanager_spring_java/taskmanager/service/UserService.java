package com.rajat.taskmanager_spring_java.taskmanager.service;

import com.rajat.taskmanager_spring_java.taskmanager.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {


    public User findUserById(String userId) ;

    public List<User> findAllUsers();

    public UserDetails loadUserByUsername(String username);

}