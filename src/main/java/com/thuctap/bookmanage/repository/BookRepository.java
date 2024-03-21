package com.thuctap.bookmanage.repository;

import com.thuctap.bookmanage.entity.ListBook;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookRepository extends JpaRepository<ListBook,Long> {
    @Query("SELECT c FROM ListBook c where c.user.id_user = ?1")
    List<ListBook> findByIdUser(Long id_user);

    @Query("SELECT c FROM ListBook c where c.name_book = ?1")
    ListBook findByName(String name);

    @Query("SELECT c FROM ListBook c where c.id_list = ?1")
    ListBook findByID(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM ListBook c WHERE c.id_list=?1")
    void deleteBookById(Long id);

    @Query("SELECT c FROM ListBook c WHERE LOWER(c.name_book) LIKE LOWER(?1) OR LOWER(c.description) LIKE LOWER(?1)")
    List<ListBook> findBook(String name);


    @Query("SELECT c FROM ListBook c ORDER BY c.day DESC ")
    List<ListBook> showAllBook();

    Page<ListBook> findAll(Pageable pageable);
    @Query("SELECT c FROM ListBook c ORDER BY c.count_view DESC ")
    List<ListBook> showHotBook();

}
