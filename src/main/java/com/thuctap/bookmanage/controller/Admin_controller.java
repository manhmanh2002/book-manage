package com.thuctap.bookmanage.controller;

import com.thuctap.bookmanage.entity.*;
import com.thuctap.bookmanage.repository.*;
import com.thuctap.bookmanage.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.io.File;
import java.util.Arrays;
import java.util.List;


@Controller
public class Admin_controller {
    @Autowired
    private AdminService adminService;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DirService dirService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private Type_BookRepository type_bookRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private ChapterRepository chapterRepository;

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "Admin_user")
    public String Admin_user(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        if (session.getAttribute("id") == null){
            model.addAttribute("user",new User());
            return "login";
        }
        model.addAttribute("list_user",adminService.showAllUser());
        model.addAttribute("id",session.getAttribute("id"));
        return "Admin_user";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @RequestMapping(value="Admin_book")
    public String Admin_book(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        List<ListBook> list_book = bookService.showAllBook();
        model.addAttribute("list",list_book);
        model.addAttribute("id",new User());
        if(session.getAttribute("id") == null){
            return "login";
        }
        return "Admin_book";
    }
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @PostMapping(value = "delete_user")
    public String deleteUser(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        if (session.getAttribute("id") == null){
            model.addAttribute("user",new User());
            return "login";
        }
        String id = session.getAttribute("id").toString();
        User admin = adminService.findUserById(Long.parseLong(id));
        User user = adminService.findUserById(Long.parseLong(request.getParameter("id_user")));
        String currentDirectory = System.getProperty("user.dir");
        if((admin.getRole() == Role.SUPERADMIN || admin.getRole() == Role.ADMIN & user.getRole() == Role.USER) || user.getRole() == Role.ADMIN & admin.getRole() == Role.SUPERADMIN){
            List<ListBook> listBook = bookService.findByIdUser(user.getId_user());
            for(ListBook x:listBook){
                historyRepository.deleteHistoryByIdUserOrIdBook(user.getId_user(), x.getId_list());
                favoriteRepository.deleteFavoriteByIdUserOrIdBook(user.getId_user(), x.getId_list());
                bookService.deleteBookById(x.getId_list());
                type_bookRepository.deleteByIdBook(x.getId_list());
                File file = new File(currentDirectory+"/data/" + x.getId_list());
                dirService.deleteDir(file);
            }
            adminService.deleteUserByAdmin(user.getId_user());
        }
        model.addAttribute("list_user",adminService.showAllUser());
        model.addAttribute("id",session.getAttribute("id"));
        return "Admin_user";
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @PostMapping(value = "uplevel")
    public String upPower(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        if(session.getAttribute("id")==null){
            model.addAttribute("user",new User());
            return "login";
        }
        String id = session.getAttribute("id").toString();

        User admin = userService.findUser(Long.parseLong(id));
        User user = userService.findUser(Long.parseLong(request.getParameter("id")));
        if(admin.getRole() == Role.SUPERADMIN || admin.getRole() == Role.ADMIN & user.getRole() == Role.USER){
            user.setRole(Role.ADMIN);
            userRepository.save(user);
        } else if (admin.getRole()==Role.SUPERADMIN & user.getRole() == Role.ADMIN) {
            user.setRole(Role.SUPERADMIN);
            userRepository.save(user);
        }
        List<User> allUser = userRepository.showAllUser();
        model.addAttribute("list_user",allUser);
        model.addAttribute("id",session.getAttribute("id"));
        return "Admin_user";
    }
    @PreAuthorize("hasAnyAuthority('SUPERADMIN','ADMIN')")
    @RequestMapping(value = "downlevel")
    public String downPower(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        if(session.getAttribute("id")==null){
            model.addAttribute("user",new User());
            return "login";
        }
        String id = session.getAttribute("id").toString();
        User admin = userService.findUser(Long.parseLong(id));
        User user = userService.findUser(Long.parseLong(request.getParameter("id")));
        if(admin.getRole() == Role.SUPERADMIN & user.getRole() == Role.ADMIN ){
            user.setRole(Role.USER);
            userRepository.save(user);
        }
        List<User> allUser = userRepository.showAllUser();
        model.addAttribute("list_user",allUser);
        model.addAttribute("id",session.getAttribute("id"));
        return "Admin_user";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @PostMapping("/delete_book_admin")
    public String deleteBookByAuthor(HttpServletRequest request,Model model){
        String currentDirectory = System.getProperty("user.dir");
        String id_delete = request.getParameter("id_book");
        File file = new File(currentDirectory+"/data/"+id_delete);
        bookService.deleteBook(Long.parseLong(id_delete));
        dirService.deleteDir(file);
        Admin_book(request,model);
        return "Admin_book";
    }

    @RequestMapping(value="selectbook_admin")
    public String select_book( HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        String id = request.getParameter("id_book");
        List<Chapter> list = chapterService.showAllChapterById(Long.parseLong(id));
        session.setAttribute("id_book",id);
        if(list.isEmpty()){
            model.addAttribute("message", "Not available yet");
        }
        model.addAttribute("id",session.getAttribute("id"));
        model.addAttribute("id_book",id);
        model.addAttribute("chapters",list);
        return "Admin_chapter";
    }
    @RequestMapping("selectchapter_admin")
    public String selectChapter(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        String chapter = request.getParameter("id_chapter");
        if(chapter.equals("0")) {
            chapter = request.getParameter("test");
        }
        String id_book = session.getAttribute("id_book").toString();

        List<Picture> list = pictureRepository.showBook(Long.parseLong(chapter));
        List<Chapter> chapters = chapterService.showAllChapterById(Long.parseLong(id_book));
        Chapter chap = chapterService.findByChapterandID(Long.parseLong(id_book), Long.parseLong(chapter));
        model.addAttribute("id_book", id_book);
        model.addAttribute("id_chapter", chapter);
        model.addAttribute("books", list);
        model.addAttribute("chapter", chap);
        model.addAttribute("chapters", chapters);
        model.addAttribute("id",session.getAttribute("id"));
        return "setupchapter";
    }
    @PostMapping("/delete_chapter_admin")
    public String deleteChapterByAuthor(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        String id_delete = request.getParameter("id_chapter");
        String id_book = session.getAttribute("id_book").toString();
        
        Chapter chapter = chapterRepository.findChapterById(Long.parseLong(id_delete));

        String currentDirectory = System.getProperty("user.dir");
        File file = new File(currentDirectory + "/data/" + id_book+"/"+id_delete);
        
        if(chapter.getId_chapter() == chapter.getPreChap()){
            Chapter chapter_First = chapterRepository.findChapterById(chapter.getNextChap());
            chapter_First.setPreChap(chapter_First.getId_chapter());
            chapterRepository.save(chapter_First);
        } else if (chapter.getId_chapter() == chapter.getNextChap()){
            Chapter chapter_Last = chapterRepository.findChapterById(chapter.getPreChap());
            chapter_Last.setNextChap(chapter_Last.getId_chapter());
            chapterRepository.save(chapter_Last);
        } else {
            Chapter chapter_next = chapterRepository.findChapterById(chapter.getNextChap());
            Chapter chapter_prev = chapterRepository.findChapterById(chapter.getPreChap());
            chapter_next.setPreChap(chapter_prev.getId_chapter());
            chapter_prev.setNextChap(chapter_next.getId_chapter());
            chapterRepository.saveAll(Arrays.asList(chapter_prev,chapter_next));
        }
        bookService.deleteChapter(Long.parseLong(id_delete));
        dirService.deleteDir(file);
        ListBook book = bookService.findByID(chapter.getBook().getId_list());
        book.setCount_chapter(book.getCount_chapter()-1);
        bookService.saveBook(book);
        select_book(request,model);
        return "Admin_chapter";
    }

}
