package com.thuctap.bookmanage.service;

import com.thuctap.bookmanage.entity.*;
import com.thuctap.bookmanage.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class BookService {
    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    private final ChapterRepository chapterRepository;

    @Autowired
    private final HistoryRepository historyRepository;

    @Autowired
    private final Type_BookRepository type_bookRepository;

    @Autowired
    private final PictureRepository pictureRepository;

    public List<ListBook> showAllBook() {
        return bookRepository.showAllBook();
    }

    public ListBook findByName(String name) {
        return bookRepository.findByName(name);
    }

    public ListBook findById(Long id) {
        return bookRepository.findByID(id);
    }

    public List<ListBook> findBook(String name) {
        return bookRepository.findBook(name);
    }

    public List<ListBook> findByIdUser(Long id) {
        return bookRepository.findByIdUser(id);
    }

    public void deleteBookById(Long id) {
        bookRepository.deleteBookById(id);
    }

    public void saveBook(ListBook book) {
        bookRepository.save(book);
    }
    @Transactional
    @Modifying
    public void deleteBook(Long id_book) {
        List<Chapter> chapters = chapterRepository.showAllChapterById(id_book);
        for (Chapter chapter : chapters) {
            deleteChapter(chapter.getId_chapter());
        }
        type_bookRepository.deleteByIdBook(id_book);
        bookRepository.deleteBookById(id_book);
    }
    @Transactional
    @Modifying
    public void deleteChapter(Long id_chapter) {
        List<Picture> list = pictureRepository.showBook(id_chapter);
        for (Picture picture : list) {
            deleteOnePage(picture.getId());
        }
        chapterRepository.deleteChapterById(id_chapter);
    }
    @Transactional
    @Modifying
    public void deleteOnePage(Long id_page) {
        pictureRepository.deleteOnePageById(id_page);
    }

    public List<ListBook> showHistory(HttpServletRequest request){
        HttpSession session = request.getSession();
        List<Long> list = historyRepository.findByIduser(Long.parseLong(session.getAttribute("id").toString()));
        List<ListBook> listBooks = new ArrayList<ListBook>();
        for(Long x:list){
            listBooks.add(bookRepository.findByID(x));
        }
        return listBooks;
    }


}
