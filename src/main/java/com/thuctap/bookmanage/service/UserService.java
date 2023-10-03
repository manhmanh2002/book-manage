package com.thuctap.bookmanage.service;

import com.thuctap.bookmanage.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{
    public User checkEmail(String email);
    public User findUser(long id);
    public User get(String resetPasswordToken);

    public void updatePassword(User user, String newPassword);

    public User changePass(User user,String email ,String oldpass,String newpass, HttpServletRequest request);
    public User forgotPass(User user,String email,HttpServletRequest request);

}
