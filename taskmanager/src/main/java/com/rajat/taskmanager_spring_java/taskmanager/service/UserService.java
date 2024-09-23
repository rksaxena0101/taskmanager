package com.rajat.taskmanager_spring_java.taskmanager.service;

import com.rajat.taskmanager_spring_java.taskmanager.entity.User;
import java.util.List;

public interface UserService {

    public User findUserProfileByJwt(String jwt);

    public User findUserById(String userId) ;

    public List<User> findAllUsers();

    User findUserByUsername(String username);
}