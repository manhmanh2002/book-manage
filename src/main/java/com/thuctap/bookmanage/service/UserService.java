package com.thuctap.bookmanage.service;

import com.thuctap.bookmanage.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{
    User checkEmail(String email);
    User findUser(long id);
    User get(String resetPasswordToken);
    boolean oldPasswordValid(User user,String oldPass);
    void updatePassword(User user, String newPassword);

    User changePass(String email,HttpServletRequest request);
    User forgotPass(String email, HttpServletRequest request);

}
