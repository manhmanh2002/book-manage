package com.thuctap.bookmanage.controller;

import com.thuctap.bookmanage.common.Constants.CACHE;
import com.thuctap.bookmanage.convert.BookConverter;
import com.thuctap.bookmanage.dao.request.ListBookDTO;
import com.thuctap.bookmanage.dao.request.ListTypeDTO;
import com.thuctap.bookmanage.entity.*;
import com.thuctap.bookmanage.repository.*;
import com.thuctap.bookmanage.service.BookService;
import com.thuctap.bookmanage.service.ChapterService;
import com.thuctap.bookmanage.service.RedisService;
import com.thuctap.bookmanage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Slf4j
public class BookController {
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private Type_BookRepository type_bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private BookRepository list_bookRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private BookConverter bookConverter;

    @Autowired
    private RedisService redisService;
    @RequestMapping("/book-reading")
    public String ReadBook(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String key = CACHE.REDIS_GET_ALL_BOOK;
        ListBookDTO listBooks;
        if (redisService.get(key) != null) {
            listBooks = (ListBookDTO) redisService.get(key);
            log.info("Get by redis");
        } else {
            List<ListBook> list = list_bookRepository.showAllBook();
            listBooks = bookConverter.convert(list);
            log.info("Get by db");
            redisService.put(key,listBooks);
        }
        String keyType = CACHE.REDIS_GET_ALL_TYPE;
        ListTypeDTO list_type;
        if (redisService.get(keyType) != null) {
            list_type = (ListTypeDTO) redisService.get(keyType);
            log.info("Get by redis");
        } else {
            List<Type> list = typeRepository.showAllCategory();
            list_type = bookConverter.convertToType(list);
            log.info("Get by db");
            redisService.put(keyType,list_type);
        }
        session.setAttribute("id_book",null);
        model.addAttribute("list_book", listBooks.getBookDTOList());

        model.addAttribute("categories",list_type.getTypeDTOS());
        if(session.getAttribute("id") != null){
            model.addAttribute("history_book",bookService.showHistory(request));
            model.addAttribute("id",session.getAttribute("id").toString());
            return "list_book";
        }
        return "index";

    }

    @RequestMapping("/home/search")
    public String searchBook(HttpServletRequest request, Model model){
        String name = request.getParameter("search");
        HttpSession session = request.getSession();
        List<ListBook> books = list_bookRepository.findBook(name);
        String keyType = CACHE.REDIS_GET_ALL_TYPE;
        ListTypeDTO list_type;
        if (redisService.get(keyType) != null) {
            list_type = (ListTypeDTO) redisService.get(keyType);
            log.info("Get by redis");
        } else {
            List<Type> list = typeRepository.showAllCategory();
            list_type = bookConverter.convertToType(list);
            log.info("Get by db");
            redisService.put(keyType,list_type);
        }
        model.addAttribute("list_book",books);
        model.addAttribute("categories",list_type.getTypeDTOS());
        model.addAttribute("history_book",bookService.showHistory(request));
        if(session.getAttribute("id") != null){
            model.addAttribute("id", session.getAttribute("id"));
        }
        return "list_book";
    }
    @RequestMapping("/selectCategory")
    public String selectCategory(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        model.addAttribute("user",new User());
        String value = request.getParameter("id_category");
        String keyType = CACHE.REDIS_GET_ALL_TYPE;
        ListTypeDTO list_type;
        if (redisService.get(keyType) != null) {
            list_type = (ListTypeDTO) redisService.get(keyType);
            log.info("Get by redis");
        } else {
            List<Type> list = typeRepository.showAllCategory();
            list_type = bookConverter.convertToType(list);
            log.info("Get by db");
            redisService.put(keyType,list_type);
        }        model.addAttribute("categories",list_type.getTypeDTOS());
        if(!value.equals("0")){
            List<Long> list_id = type_bookRepository.findByCategory(Long.parseLong(value));
            List<ListBook> list_book = new ArrayList<>();
            for(Long x:list_id){
                list_book.add(list_bookRepository.findByID(x));
            }
            model.addAttribute("history_book",bookService.showHistory(request));
            model.addAttribute("list_book",list_book);
        } else {
            String key = CACHE.REDIS_GET_ALL_BOOK;
            ListBookDTO listBooks;
            if (redisService.get(key) != null) {
                listBooks = (ListBookDTO) redisService.get(key);
                log.info("Get by redis");
            } else {
                List<ListBook> list = list_bookRepository.showAllBook();
                listBooks = bookConverter.convert(list);
                log.info("Get by db");
                redisService.put(key,listBooks);
            }
            model.addAttribute("history_book",bookService.showHistory(request));
            model.addAttribute("list_book",listBooks.getBookDTOList());
        }
        if(session.getAttribute("id") != null){
            model.addAttribute("id", session.getAttribute("id"));
            return "list_book";
        }
        return "index";
    }

    @RequestMapping(value="select_book")
    public String select_book( HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        String id = request.getParameter("id_book");
        ListBook book = list_bookRepository.findByID(Long.parseLong(id));
        User author = userRepository.findUserById(book.getId_user());
        if(session.getAttribute("id") != null){
            String id_user = session.getAttribute("id").toString();
            session.setAttribute("id_book",id);
            History history = historyRepository.findByIduserAndIdbook(Long.parseLong(id_user),Long.parseLong(id));
            if(Objects.nonNull(history)){
                history.setDay(java.time.LocalDate.now());
                history.setTime(java.time.LocalTime.now());
                historyRepository.save(history);
            }else{
                if(Long.parseLong(id) != Long.parseLong(id_user)) {
                    book = list_bookRepository.findByID(Long.parseLong(id));
                    book.setCount_view(book.getCount_view() + 1);
                    list_bookRepository.save(book);
                }
                History history1 = new History();
                history1.setBook(bookService.findById(Long.parseLong(id)));
                history1.setUser(userService.findUser(Long.parseLong(id_user)));
                history1.setDay(java.time.LocalDate.now());
                history1.setTime(java.time.LocalTime.now());
                historyRepository.save(history1);
            }
        }
        book = list_bookRepository.findByID(Long.parseLong(id));
        if(session.getAttribute("id")!=null){
            model.addAttribute("id",session.getAttribute("id"));

            Favorite favorite = favoriteRepository.findByIdUserAndIdBook(Long.parseLong(session.getAttribute("id").toString()),Long.parseLong(id));
            if(Objects.nonNull(favorite)){
                model.addAttribute("follow",1);
            }else{
                model.addAttribute("follow",0);
            }
        }
        List<Chapter> list = chapterService.listChapter(Long.parseLong(id));

        model.addAttribute("book",book);
        model.addAttribute("chapters",list);
        model.addAttribute("author",author.getName());
        return "Chapter";
    }

    @RequestMapping("/selectchapter")
    public String selectChapter(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        String id_chapter = request.getParameter("id_chapter");

        List<Picture> list = pictureRepository.showBook(Long.parseLong(id_chapter));
        String id_book;
        if(session.getAttribute("id_book")!=null){
            id_book = session.getAttribute("id_book").toString();
        }else{
            id_book = request.getParameter("id_book");
        }

        List<Chapter> chapters = chapterService.listChapter(Long.parseLong(id_book));

        Chapter chap = chapterRepository.findByChapterandID(Long.parseLong(id_book), Long.parseLong(id_chapter));
        if(session.getAttribute("id") != null){
            model.addAttribute("id", session.getAttribute("id"));
        }
        model.addAttribute("id_book", id_book);
        model.addAttribute("id_chapter", id_chapter);
        model.addAttribute("books", list);
        model.addAttribute("chapter", chap);
        model.addAttribute("chapters", chapters);
        model.addAttribute("id",session.getAttribute("id"));
        return "read_book";
    }
    @RequestMapping("/hot")
    public String hotBook(Model model,HttpServletRequest request){
        HttpSession session = request.getSession();
        List<Type> list_type = typeRepository.showAllCategory();
        model.addAttribute("categories",list_type);
        model.addAttribute("list_book",bookService.showHotBook());
        if(session.getAttribute("id")!=null){
            model.addAttribute("id",session.getAttribute("id"));
            model.addAttribute("history_book",bookService.showHistory(request));
            return "list_book";
        }
        return "/index";

    }

    @RequestMapping("favorite")
    public String favorite(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        if(session.getAttribute("id") == null){
            model.addAttribute("message","You need login to user function!!!");
            model.addAttribute("user",new User());
            return "/login";
        }
        Long id = Long.parseLong(session.getAttribute("id").toString());
        model.addAttribute("id", session.getAttribute("id"));
        List<Type> list_type = typeRepository.showAllCategory();
        model.addAttribute("categories",list_type);
        model.addAttribute("history_book",bookService.showHistory(request));
        List<Long> list_id = favoriteRepository.findListBookFavorite(id);
        List<ListBook> list_book = new ArrayList<>();
        for(Long x:list_id){
            list_book.add(list_bookRepository.findByID(x));
        }
        model.addAttribute("list_book",list_book);
        return "list_book";
    }
    @RequestMapping("addtoFavorite")
    public String addtoFavorite(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Long id = Long.parseLong(session.getAttribute("id").toString());
        String id_book = request.getParameter("id_book");
        Favorite favorite = favoriteRepository.findByIdUserAndIdBook(id,Long.parseLong(id_book));
        if(Objects.nonNull(favorite)){
            favorite.setDay(java.time.LocalDate.now());
            favorite.setTime(java.time.LocalTime.now());
            favoriteRepository.save(favorite);
        }else{
            Favorite favoriteNew = new Favorite();
            favoriteNew.setUser(userService.findUser(id));
            favoriteNew.setBook(bookService.findById(Long.parseLong(id_book)));
            favoriteNew.setDay(java.time.LocalDate.now());
            favoriteNew.setTime(java.time.LocalTime.now());
            favoriteRepository.save(favoriteNew);
        }
        select_book(request, model);
        return "Chapter";
    }

    @RequestMapping("deletefromFavorite")
    public String deletefromFavorite(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Long id = Long.parseLong(session.getAttribute("id").toString());
        String id_book = request.getParameter("id_book");
        Favorite favorite = favoriteRepository.findByIdUserAndIdBook(id,Long.parseLong(id_book));
        if(Objects.nonNull(favorite)){
            favoriteRepository.deleteFavoriteByIdUserAndIdBook(id,Long.parseLong(id_book));
        }
        select_book(request, model);
        return "Chapter";
    }
}
