package com.thuctap.bookmanage.repository;

import com.thuctap.bookmanage.entity.Favorite;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    @Query("SELECT c FROM Favorite c WHERE c.user.id_user=?1 AND c.book.id_list=?2")
    Favorite findByIdUserAndIdBook(Long id_user,Long id_list);

    @Transactional
    @Modifying
    @Query("DELETE FROM Favorite c WHERE c.user.id_user=?1 AND c.book.id_list=?2")
    void deleteFavoriteByIdUserAndIdBook(Long id_user,Long id_list);

    @Transactional
    @Modifying
    @Query("DELETE FROM Favorite c WHERE c.book.id_list=?1 ")
    void deleteFavoriteByIDBook(Long id_list);

    @Query("SELECT c.book.id_list FROM Favorite c WHERE c.user.id_user=?1 ORDER BY c.day DESC, c.time DESC ")
    List<Long> findListBookFavorite(Long id);
    @Transactional
    @Modifying
    @Query("SELECT c FROM Favorite c WHERE c.user.id_user=?1 OR c.book.id_list=?2")
    void deleteFavoriteByIdUserOrIdBook(Long idUser, Long idList);

}
