package com.rajat.taskmanager_spring_java.taskmanager.service.imp;

import com.rajat.taskmanager_spring_java.taskmanager.repository.UserRepository;
import com.rajat.taskmanager_spring_java.taskmanager.entity.User;
import com.rajat.taskmanager_spring_java.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
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
        //System.out.println("loadUserByUsername::username:-"+username);
        User user = userRepository.findByEmail(username);
        //System.out.println("User::"+user);
        users.add(user);

        if(user==null) {
            throw new UsernameNotFoundException("User not found with this email" + username);
        }
        //System.out.println("Loaded user: " + user.getEmail() + ", Role: " + user.getRole());
        List<GrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    @Override
    public User findUserById(String userId) {
        return users.get(Integer.parseInt(userId));
    }

    @Override
    public List<User> findAllUsers() {
        return users;
    }

    @Override
    public User findUserByUsername(String username) {
        return null;
    }

    @Override
    public User findUserProfileByJwt(String jwt) {
        return null;
    }
}