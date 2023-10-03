package com.thuctap.bookmanage.repository;

import com.thuctap.bookmanage.entity.Chapter;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
@Repository
public interface ChapterRepository extends JpaRepository<Chapter,Long> {
    @Query("SELECT c FROM Chapter c WHERE c.id_chapter=?1 ORDER BY c.id_chapter")
    public List<Chapter> showAllChapterById(Long idBook);

    @Transactional
    @Modifying
    @Query("DELETE FROM Chapter c WHERE c.id_chapter=?1")
    public void deleteChapterById(Long idChapter);

    @Query("SELECT c FROM Chapter c WHERE c.book.id_list=?1 AND c.preChap = c.id_chapter")
    Chapter findFirstChapter(Long id_list);

    @Query("SELECT c FROM Chapter c WHERE c.id_chapter=?1")
    Chapter findChapterById(Long nextChap);

    @Query("SELECT c FROM Chapter c WHERE c.book.id_list=?1 AND c.id_chapter=?2")
    Chapter findByChapterandID(Long id_book,Long id_chapter);


    @Query("SELECT c FROM Chapter c WHERE c.book.id_list=?1 AND c.number=?2")
    Chapter findByNumberandID(long l, int i);
    @Query("SELECT c FROM Chapter c WHERE c.book.id_list=?1 AND c.nextChap = c.id_chapter")
    Chapter findNewChapter(Long id);
    @Query("SELECT MAX(c.id_chapter) FROM Chapter c")
    Long findMaxId();

}
