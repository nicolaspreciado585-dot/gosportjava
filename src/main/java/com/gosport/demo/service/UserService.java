package com.gosport.demo.service;

import java.util.List;

import com.gosport.demo.model.User;


public interface UserService {

  
    User saveUser(User user);
    
    User findUserById(Long id);
    
    List<User> findAllUsers();
    
   
    User authenticateUser(String email, String password);
}