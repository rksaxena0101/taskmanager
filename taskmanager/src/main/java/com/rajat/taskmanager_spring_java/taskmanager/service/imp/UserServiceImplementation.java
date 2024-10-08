package com.rajat.taskmanager_spring_java.taskmanager.service.imp;

import com.rajat.taskmanager_spring_java.taskmanager.repository.UserRepository;
import com.rajat.taskmanager_spring_java.taskmanager.entity.User;
import com.rajat.taskmanager_spring_java.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImplementation implements UserService, UserDetailsService {

    public List<User> users = new ArrayList<>();

    @Autowired
    private UserRepository userRepository;

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository=userRepository;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with this email: " + username);
        }

        // Use the user directly instead of creating a new UserDetails object
        return user;
    }

    @Override
    public User findUserById(String userId) {
        return users.get(Integer.parseInt(userId));
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

}