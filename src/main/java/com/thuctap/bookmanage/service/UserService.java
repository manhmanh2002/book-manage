package com.thuctap.bookmanage.service;

import com.thuctap.bookmanage.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;

public interface UserService extends UserDetailsService{
    public User checkEmail(String email);
    public User findUser(long id);
    public User get(String resetPasswordToken);
    public boolean oldPasswordValid(User user,String oldPass);
    public void updatePassword(User user, String newPassword);

    public User changePass(String email,HttpServletRequest request);
    public User forgotPass(String email, HttpServletRequest request);

}
