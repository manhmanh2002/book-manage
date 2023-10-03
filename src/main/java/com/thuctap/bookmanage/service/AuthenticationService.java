package com.thuctap.bookmanage.service;

import com.thuctap.bookmanage.dao.request.SignupRequest;
import com.thuctap.bookmanage.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {

    User signUp(SignupRequest signupRequest, HttpServletRequest request);
    void logOut();


}
