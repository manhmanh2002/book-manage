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

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private Type_BookRepository type_bookRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

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

    public void deleteBook(Long id_book) {
        favoriteRepository.deleteFavoriteByIDBook(id_book);
        historyRepository.deleteHistoryByIdBook(id_book);
        type_bookRepository.deleteByIdBook(id_book);
        List<Chapter> chapters = chapterRepository.showAllChapterById(id_book);
        System.out.println(chapters);
        for (Chapter chapter : chapters) {
            deleteChapter(chapter.getId_chapter());
        }
        bookRepository.deleteBookById(id_book);
    }

    public void deleteChapter(Long id_chapter) {
        List<Picture> list = pictureRepository.showBook(id_chapter);
        for (Picture picture : list) {
            deleteonepage(picture.getId());
        }
        chapterRepository.deleteChapterById(id_chapter);
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
    public void deleteonepage(Long id_page) {
        pictureRepository.deleteOnePageById(id_page);
    }

    public List<ListBook> showHotBook() {
        return bookRepository.showHotBook();
    }

    public ListBook findByID(Long idList) {
        return bookRepository.findByID(idList);
    }
}
