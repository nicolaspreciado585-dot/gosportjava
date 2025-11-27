package com.gosport.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gosport.demo.model.User;


public interface UserRepository extends JpaRepository<User, Long> {

    
    User findByEmail(String email);

}