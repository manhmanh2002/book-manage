package com.thuctap.bookmanage.repository;

import com.thuctap.bookmanage.entity.Picture;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {
    @Query("SELECT c FROM Picture c WHERE c.chapter.id_chapter =?1 order by c.id")
    public List<Picture> showBook(Long idChapter);

    @Transactional
    @Modifying
    @Query("DELETE FROM Picture c WHERE c.id=?1")
    void deleteOnePageById(Long idPage);
}
