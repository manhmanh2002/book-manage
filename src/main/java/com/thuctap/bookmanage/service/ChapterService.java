package com.thuctap.bookmanage.service;

import com.thuctap.bookmanage.entity.Chapter;
import com.thuctap.bookmanage.entity.ListBook;
import com.thuctap.bookmanage.repository.BookRepository;
import com.thuctap.bookmanage.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ChapterService {
    
    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private BookRepository bookRepository;

    public Chapter createNextChap(Long id_chapter_prev, Long id_book) {
        Chapter chapter_prev = chapterRepository.findChapterById(id_chapter_prev);
        Chapter chapter = new Chapter();
        ListBook book = bookRepository.findByID(id_book);
        if (chapter_prev.getNextChap() == chapter_prev.getId_chapter()) {
            chapter = chapterRepository.save(new Chapter(book, 0, "", 0,
                    chapter_prev.getId_chapter(), 0l, java.time.LocalDate.now(), java.time.LocalTime.now()));
            chapter.setNextChap(chapter.getId_chapter());
            chapter_prev.setNextChap(chapter.getId_chapter());
            chapterRepository.saveAll(Arrays.asList(chapter, chapter_prev));
        } else {
            chapter = chapterRepository.save(new Chapter(book, 0, "", 0,
                    chapter_prev.getId_chapter(), chapter_prev.getNextChap(), java.time.LocalDate.now(),
                    java.time.LocalTime.now()));
            Chapter chapter_next = chapterRepository.findChapterById(chapter_prev.getNextChap());
            chapter_prev.setNextChap(chapter.getId_chapter());
            chapter_next.setPreChap(chapter.getId_chapter());
            chapterRepository.saveAll(Arrays.asList(chapter_next, chapter_prev));
        }
        return chapter;
    }

    public Chapter createPrevChap(Long id_chapter_next, Long id_book) {
        Chapter chapter_next = chapterRepository.findChapterById(id_chapter_next);
        Chapter chapter = new Chapter();
        ListBook book = bookRepository.findByID(id_book);
        if (chapter_next.getPreChap() == chapter_next.getId_chapter()) {
            chapter = chapterRepository.save(new Chapter(book, 0, "", 0,
                    0l, chapter_next.getId_chapter(), java.time.LocalDate.now(), java.time.LocalTime.now()));
            chapter.setPreChap(chapter.getId_chapter());
            chapter_next.setPreChap(chapter.getId_chapter());
            chapterRepository.saveAll(Arrays.asList(chapter, chapter_next));
        } else {
            chapter = chapterRepository.save(new Chapter(book, 0, "", 0,
                    chapter_next.getPreChap(), chapter_next.getId_chapter(), java.time.LocalDate.now(),
                    java.time.LocalTime.now()));
            Chapter chapter_prev = chapterRepository.findChapterById(chapter_next.getPreChap());
            chapter_next.setPreChap(chapter.getId_chapter());
            chapter_prev.setNextChap(chapter.getId_chapter());
            chapterRepository.saveAll(Arrays.asList(chapter_next, chapter_prev));
        }
        return chapter;
    }

    public List<Chapter> listChapter(Long id_book) {
        List<Chapter> list = new ArrayList<>();
        Chapter chapter = chapterRepository.findFirstChapter(id_book);
        if(Objects.nonNull(chapter)) {
            while (chapter.getNextChap() != chapter.getId_chapter()) {
                list.add(chapter);
                chapter = chapterRepository.findChapterById(chapter.getNextChap());
            }
            list.add(chapter);
        }
        return list;
    }

    public List<Chapter> showAllChapterById(long l) {
        return chapterRepository.showAllChapterById(l);
    }

    public Chapter findByChapterandID(long l, long l1) {
        return chapterRepository.findByChapterandID(l,l1);
    }
}
