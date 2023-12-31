package com.thuctap.bookmanage.controller;

import com.thuctap.bookmanage.entity.*;
import com.thuctap.bookmanage.repository.*;
import com.thuctap.bookmanage.service.BookService;
import com.thuctap.bookmanage.service.ChapterService;
import com.thuctap.bookmanage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
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
    @RequestMapping("/book-reading")
    public String ReadBook( HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        List<ListBook> all_book = list_bookRepository.showAllBook();
        session.setAttribute("id_book",null);
        model.addAttribute("list_book",all_book);
        List<Type> list_type = typeRepository.showAllCategory();
        model.addAttribute("categories",list_type);
        if(session.getAttribute("id") != null){
            model.addAttribute("history_book",bookService.showHistory(request));
            model.addAttribute("id",session.getAttribute("id").toString());
            return "list_book";
        }
        return "index";

    }

    @RequestMapping("/search_book")
    public String searchBook(HttpServletRequest request, Model model){
        String name = request.getParameter("search");
        List<ListBook> books = list_bookRepository.findBook(name);
        List<Type> list_type = typeRepository.showAllCategory();
        model.addAttribute("list_books",books);
        model.addAttribute("categories",list_type);
        model.addAttribute("history_book",bookService.showHistory(request));
        return "list_book";
    }
    @RequestMapping("/selectCategory")
    public String selectCategory(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        model.addAttribute("user",new User());
        String value = request.getParameter("id_category");
        List<Type> list_type = typeRepository.showAllCategory();
        System.out.println(list_type);
        model.addAttribute("categories",list_type);
        if(!value.equals("0")){
            List<Long> list_id = type_bookRepository.findByCategory(Long.parseLong(value));
            List<ListBook> list_book = new ArrayList<>();
            for(Long x:list_id){
                list_book.add(list_bookRepository.findByID(x));
            }
            model.addAttribute("list_book",list_book);
        } else {
            List<ListBook> list_book = list_bookRepository.showAllBook();
            model.addAttribute("list_book",list_book);
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
