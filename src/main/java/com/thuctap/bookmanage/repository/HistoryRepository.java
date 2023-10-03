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
    @Query("SELECT c FROM History c WHERE c.id_user=?1 AND c.id_book=?2")
    History findByIduserAndIdbook(Long id_user,Long id_manga);

    @Query("SELECT c.id_book FROM History c WHERE c.id_user=?1 order by c.day DESC ,c.time DESC ")
    List<Long> findByIduser(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM History c WHERE c.id_user=?1 OR c.id_book=?2")
    void deleteHistoryByIdUserOrIdManga(Long id_user,Long id_book);
    @Transactional
    @Modifying
    @Query("DELETE FROM History c WHERE c.id_book=?1")
    void deleteHistoryByIdManga(Long id_book);

}
