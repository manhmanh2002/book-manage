package com.thuctap.bookmanage.service;

import com.thuctap.bookmanage.controller.Utility;
import com.thuctap.bookmanage.dao.request.SignupRequest;
import com.thuctap.bookmanage.entity.Enable;
import com.thuctap.bookmanage.entity.Role;
import com.thuctap.bookmanage.entity.User;
import com.thuctap.bookmanage.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserServiceImp userServiceImp;
    @Override
    public User signUp(SignupRequest signupRequest, HttpServletRequest request) {
        String token = UUID.randomUUID().toString();
        User user = User.builder()
                .name(signupRequest.getName())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .email(signupRequest.getEmail())
                .gender(signupRequest.getGender())
                .role(Role.USER)
                .token(token)
                .photo(signupRequest.getPhoto())
                .enable(Enable.FALSE)
                .build();
        String link = Utility.getSiteURL(request) + "/confirm_account?token=" + user.getToken();
        userServiceImp.sendEmail(user, userServiceImp.sendVerificationEmail(link));
        return userRepository.save(user);
    }
    @Override
    public void logOut() {
        SecurityContextHolder.clearContext();
    }
}
