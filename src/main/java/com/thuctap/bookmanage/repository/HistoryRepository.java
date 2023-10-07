package com.thuctap.bookmanage.repository;

import com.thuctap.bookmanage.entity.History;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History,Long> {
    @Query("SELECT c FROM History c WHERE c.user.id_user=?1 AND c.book.id_list=?2")
    History findByIduserAndIdbook(Long id_user,Long id_list);

    @Query("SELECT c.book.id_list FROM History c WHERE c.user.id_user=?1 order by c.day DESC ,c.time DESC ")
    List<Long> findByIduser(Long id_user);

    @Transactional
    @Modifying
    @Query("DELETE FROM History c WHERE c.user.id_user=?1 OR c.book.id_list=?2")
    void deleteHistoryByIdUserOrIdBook(Long id_user,Long id_book);
    @Transactional
    @Modifying
    @Query("DELETE FROM History c WHERE c.book.id_list=?1")
    void deleteHistoryByIdBook(Long id_book);

}
