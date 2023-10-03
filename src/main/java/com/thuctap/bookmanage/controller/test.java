package com.thuctap.bookmanage.controller;

import com.thuctap.bookmanage.entity.User;
import com.thuctap.bookmanage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@AllArgsConstructor
@RestController
public class test {
    private UserRepository userRepository;
    @PostMapping(value="test")
    public List<User> Admin_user(){

        return userRepository.showAllUser();
    }
}
