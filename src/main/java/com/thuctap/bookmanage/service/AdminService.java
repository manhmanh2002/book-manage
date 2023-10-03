package com.thuctap.bookmanage.service;

import com.thuctap.bookmanage.entity.User;
import com.thuctap.bookmanage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@AllArgsConstructor
@Service
public class AdminService {
    private final UserRepository userRepository;

    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    public List<User> showAllUser() {
        return userRepository.showAllUser();
    }

    public void deleteUserByAdmin(Long idUser) {
        userRepository.deleteUserByAdmin(idUser);
    }
}
