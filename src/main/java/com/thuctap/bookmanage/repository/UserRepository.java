package com.thuctap.bookmanage.repository;

import com.thuctap.bookmanage.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT c FROM User c WHERE c.email=?1 AND c.password=?2")
    User findByUsernameAndPassword(String email, String password);
    @Query("SELECT c FROM User c WHERE c.email = ?1")
    User findByEmail(String email);
    @Query("SELECT c FROM User c WHERE c.id_user=?1")
    User findByID(Long id_user);
    @Query("SELECT c FROM User c WHERE c.id_user=?1")
    User findUserById(Long id);

    @Query("SELECT c FROM User c")
    List<User> showAllUser();

    @Transactional
    @Modifying
    @Query("DELETE FROM User c WHERE c.id_user = ?1")
    void deleteUserByAdmin(Long id);
    public User findByToken(String token);
}
