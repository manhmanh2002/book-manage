package com.thuctap.bookmanage.controller;

import com.thuctap.bookmanage.entity.*;
import com.thuctap.bookmanage.config.FileUploadUtil;
import com.thuctap.bookmanage.repository.*;
import com.thuctap.bookmanage.service.BookService;
import com.thuctap.bookmanage.service.ChapterService;
import com.thuctap.bookmanage.service.DirService;
import com.thuctap.bookmanage.service.TypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Book;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
public class AuthorController {
    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private Type_BookRepository type_bookRepository;

    @Autowired
    private DirService dirService;
    
    @Autowired
    private ChapterRepository chapterRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private TypeService typeService;
    @RequestMapping("add_newbook")
    public String addNewBook(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("id") == null) {
            model.addAttribute("user", new User());
            model.addAttribute("message", "You need login to user function!!!");
            return "/login";
        }
        model.addAttribute("id", session.getAttribute("id"));
        return "create_book";
    }
    @RequestMapping("create_book")
    public String bookManager(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        if (session.getAttribute("id") == null) {
            model.addAttribute("user", new User());
            model.addAttribute("message", "You need login to user function!!!");
            return "/login";
        }
        String id_user = session.getAttribute("id").toString();
        List<ListBook> list = bookService.findByIdUser(Long.parseLong(id_user));
        model.addAttribute("list", list);
        model.addAttribute("id", id_user);
        return "list_create_book";
    }

    @PostMapping(value = "create")
    public String create_new_book(HttpServletRequest request, @RequestParam("image") MultipartFile file,
                                   @RequestParam(value = "type", required = false) String[] checkboxValue, Model model) throws IOException {
        HttpSession session = request.getSession();
        String id_user = session.getAttribute("id").toString();
        if (session.getAttribute("id") == null) {
            model.addAttribute("user", new User());
            model.addAttribute("message", "You need login to user function!!!");
            return "/login";
        }
        System.out.println(id_user);
        model.addAttribute("id", id_user);
        String name = request.getParameter("name");
        if (checkboxValue == null) {
            model.addAttribute("message", "Category is not null!!!");
            return "create_book";
        }
        if (file.isEmpty()) {
            model.addAttribute("message", "File is empty!!!");
            return "create_book";
        } else {
            if (!Objects.nonNull(bookRepository.findByName(name))) {
                String content = request.getParameter("content");
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                ListBook newBook = bookRepository.save(new ListBook(userRepository.findUserById(Long.parseLong(id_user)),
                        name, 0, fileName, content, new Date()));
                String uploadDir = "data/" + newBook.getId_list();
                FileUploadUtil.saveFile(uploadDir, fileName, file);

                if (checkboxValue.length != 0) {
                    for (String x : checkboxValue) {
                        Type_book book = new Type_book();
                        book.setBook(bookRepository.findByID(newBook.getId_list()));
                        book.setType(typeService.findbyID(Long.parseLong(x)));
                        type_bookRepository.save(book);
                    }
                }

            } else {
                model.addAttribute("message", "Name book is exist!!!");
                return "create_book";
            }
        }
        List<ListBook> list = bookRepository.findByIdUser(Long.parseLong(id_user));
        model.addAttribute("list", list);
        return "list_create_book";
    }

    @PostMapping("/delete_book")
    public String deleteBookByAuthor(HttpServletRequest request, Model model) {
        String currentDirectory = System.getProperty("user.dir");
        String id_delete = request.getParameter("id");
        File file = new File(currentDirectory + "/data/" + id_delete);
        bookService.deleteBook(Long.parseLong(id_delete));
        dirService.deleteDir(file);
        bookManager(request, model);
        return "list_create_book";
    }

    @RequestMapping("addnewchapter")
    public String new_chapter(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("id") == null) {
            model.addAttribute("user", new User());
            model.addAttribute("message", "You need login to user function!!!");
            return "/login";
        }
        model.addAttribute("id", session.getAttribute("id"));
        return "createchapter";
    }

    @RequestMapping(value = "selectbook_author")
    public String select_book(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String id = request.getParameter("id_book");
        List<Chapter> list = chapterRepository.showAllChapterById(Long.parseLong(id));
        System.out.println(list);
        session.setAttribute("id_book", id);
        if (list.isEmpty()) {
            model.addAttribute("message", "Not available yet");
        }
        model.addAttribute("id", session.getAttribute("id"));
        model.addAttribute("id_book", id);
        model.addAttribute("chapters", list);
        return "chaptermanager";
    }

    @RequestMapping("selectchapter_author")
    public String selectChapter(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String chapter = request.getParameter("id_chapter");
        if (chapter.equals("0")) {
            chapter = request.getParameter("test");
        }
        String id_book = session.getAttribute("id_book").toString();
        List<Picture> list = pictureRepository.showBook(Long.parseLong(chapter));
        List<Chapter> chapters = chapterRepository.showAllChapterById(Long.parseLong(id_book));
        Chapter chap = chapterRepository.findByChapterandID(Long.parseLong(id_book), Long.parseLong(chapter));
        model.addAttribute("id_book", id_book);
        model.addAttribute("id_chapter", chapter);
        model.addAttribute("books", list);
        model.addAttribute("chapter", chap);
        model.addAttribute("chapters", chapters);
        model.addAttribute("id", session.getAttribute("id"));
        return "setupchapter";
    }

    @PostMapping("createnewchapter")
    public String createNewChapter(HttpServletRequest request, Model model,
                                   @RequestParam("files") MultipartFile[] files) throws IOException {
        HttpSession session = request.getSession();
        String id = session.getAttribute("id").toString();
        String id_book = session.getAttribute("id_book").toString();
        List<Chapter> listchapter = chapterRepository.showAllChapterById(Long.parseLong(id_book));
        List<Picture> list = new ArrayList<>();
        String name = request.getParameter("content");
        String chap = request.getParameter("number");
        Chapter chapter = chapterRepository.findByNumberandID(Long.parseLong(id_book), Integer.parseInt(chap));
        if (!Objects.nonNull(chapter)) {
            chapter = new Chapter();
            chapter.setBook(bookRepository.findByID(Long.parseLong(id_book)));
            chapter.setNumber(Integer.parseInt(chap));
            chapter.setContent(name);
            chapter.setCount_chapter(files.length);
            chapter.setDay(java.time.LocalDate.now());
            chapter.setTime(java.time.LocalTime.now());
            if (listchapter.isEmpty()) {
                chapter.setPreChap(0l);
                chapter.setNextChap(0l);
                chapter = chapterRepository.save(chapter);
                chapter.setNextChap(chapter.getId_chapter());
                chapter.setPreChap(chapter.getId_chapter());
                chapterRepository.save(chapter);
            } else {
                Chapter prechapter = chapterRepository.findNewChapter(Long.parseLong(id_book));
                chapter.setPreChap(prechapter.getId_chapter());
                chapter.setNextChap(chapterRepository.findMaxId() + 1);
                chapter = chapterRepository.save(chapter);
                chapter.setNextChap(chapter.getId_chapter());
                chapter = chapterRepository.save(chapter);
                prechapter.setNextChap(chapter.getId_chapter());
                chapterRepository.save(prechapter);
            }
            if (files != null) {
                for (MultipartFile x : files) {
                    String fileName = StringUtils.cleanPath(x.getOriginalFilename());
                    String uploadDir = "data/" + id_book + "/" + chapter.getId_chapter();
                    FileUploadUtil.saveFile(uploadDir, fileName, x);
                    list.add(new Picture(chapter, fileName, java.time.LocalDate.now(), java.time.LocalTime.now()));}
                pictureRepository.saveAll(list);
            } else {
                model.addAttribute("message", "Image can't empty!!!");
                return "createchapter";
            }
        } else {
            model.addAttribute("message", "Error!!!");
        }

        ListBook book = bookRepository.findByID(Long.parseLong(id_book));
        book.setCount_chapter(book.getCount_chapter() + 1);
        bookRepository.save(book);
        listchapter = chapterRepository.showAllChapterById(Long.parseLong(id_book));
        model.addAttribute("id",id);
        model.addAttribute("chapters", listchapter);
        return "chaptermanager";
    }

    @RequestMapping("/addnextchapter")
    public String addNextchapter(HttpServletRequest request, Model model) {
        String id_chapter = request.getParameter("id_chapter");
        Chapter chapter = chapterRepository.findChapterById(Long.parseLong(id_chapter));
        model.addAttribute("chapter", chapter);
        return "createnextchapter";
    }

    @RequestMapping("/addprevchapter")
    public String addPrevchapter(HttpServletRequest request, Model model) {
        String id_chapter = request.getParameter("id_chapter");
        Chapter chapter = chapterRepository.findChapterById(Long.parseLong(id_chapter));
        model.addAttribute("chapter", chapter);
        return "createprevchapter";
    }

    @PostMapping("createnextchapter")
    public String createnextchapter(HttpServletRequest request, Model model,
                                    @RequestParam("files") MultipartFile[] files,
                                    @ModelAttribute("id_chapter") String id_chapter) throws IOException {
        HttpSession session = request.getSession();
        String id_book = session.getAttribute("id_book").toString();
        List<Picture> list = new ArrayList<>();
        String name = request.getParameter("content");
        String chap = request.getParameter("number");
        model.addAttribute("id", session.getAttribute("id"));
        Chapter chapter = chapterRepository.findByNumberandID(Long.parseLong(id_book), Integer.parseInt(chap));
        if (!Objects.nonNull(chapter)) {
            if (files != null) {
                chapter = chapterService.createNextChap(Long.parseLong(id_chapter), Long.parseLong(id_book));
                chapter.setContent(name);
                chapter.setNumber(Integer.parseInt(chap));
                chapter.setCount_chapter(files.length);
                chapterRepository.save(chapter);
                for (MultipartFile x : files) {
                    String fileName = StringUtils.cleanPath(x.getOriginalFilename());
                    String uploadDir = "data/" + id_book + "/" + chapter.getId_chapter();
                    FileUploadUtil.saveFile(uploadDir, fileName, x);
                    list.add(
                            new Picture(chapter, fileName, java.time.LocalDate.now(), java.time.LocalTime.now()));
                }
                pictureRepository.saveAll(list);
                ListBook book = bookRepository.findByID(Long.parseLong(id_book));
                book.setCount_chapter(book.getCount_chapter() + 1);
                bookRepository.save(book);
            } else {
                model.addAttribute("message", "Image can't empty!!!");
                model.addAttribute("chapter", chapterRepository.findChapterById(Long.parseLong(id_chapter)));
                return "createnextchapter";
            }
        } else {
            model.addAttribute("chapter", chapterRepository.findChapterById(Long.parseLong(id_chapter)));
            model.addAttribute("message", "Error!!!");
            return "createnextchapter";
        }

        List<Chapter> list_chapter = chapterService.listChapter(Long.parseLong(id_book));
        model.addAttribute("chapters", list_chapter);
        return "chaptermanager";
    }

    @PostMapping("createprevchapter")
    public String createprevchapter(HttpServletRequest request, Model model,
                                    @RequestParam("files") MultipartFile[] files,
                                    @ModelAttribute("id_chapter") String id_chapter) throws IOException {
        HttpSession session = request.getSession();
        String id_book = session.getAttribute("id_book").toString();

        List<Picture> list = new ArrayList<>();
        String name = request.getParameter("content");
        String chap = request.getParameter("number");
        model.addAttribute("id", session.getAttribute("id"));
        Chapter chapter = chapterRepository.findByNumberandID(Long.parseLong(id_book), Integer.parseInt(chap));
        if (!Objects.nonNull(chapter)) {
            if (files != null) {
                chapter = chapterService.createPrevChap(Long.parseLong(id_chapter), Long.parseLong(id_book));
                chapter.setContent(name);
                chapter.setNumber(Integer.parseInt(chap));
                chapter.setCount_chapter(files.length);
                chapterRepository.save(chapter);
                for (MultipartFile x : files) {
                    String fileName = StringUtils.cleanPath(x.getOriginalFilename());
                    String uploadDir = "data/" + id_book + "/" + chapter.getId_chapter();
                    FileUploadUtil.saveFile(uploadDir, fileName, x);
                    list.add(
                            new Picture(chapter, fileName, java.time.LocalDate.now(), java.time.LocalTime.now()));
                }
                pictureRepository.saveAll(list);
                ListBook book = bookRepository.findByID(Long.parseLong(id_book));
                book.setCount_chapter(book.getCount_chapter() + 1);
                bookRepository.save(book);
            } else {
                model.addAttribute("chapter", chapterRepository.findChapterById(Long.parseLong(id_chapter)));
                model.addAttribute("message", "Image can't empty!!!");
                return "createprevchapter";
            }
        } else {
            model.addAttribute("chapter", chapterRepository.findChapterById(Long.parseLong(id_chapter)));
            model.addAttribute("message", "Error!!!");
            return "createprevchapter";
        }

        List<Chapter> list_chapter = chapterService.listChapter(Long.parseLong(id_book));
        model.addAttribute("chapters", list_chapter);
        return "chaptermanager";
    }

    @PostMapping("/delete_chapter")
    public String deleteChapterByAuthor(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String id_delete = request.getParameter("id_chapter");
        String id_book = session.getAttribute("id_book").toString();
        Chapter chapter = chapterRepository.findChapterById(Long.parseLong(id_delete));
        String currentDirectory = System.getProperty("user.dir");
        File file = new File(currentDirectory + "/data/" + id_book + "/" + id_delete);
        if (chapter.getNumber() == chapter.getPreChap()) {
            Chapter chapter_First = chapterRepository.findChapterById(chapter.getNextChap());
            chapter_First.setPreChap(chapter_First.getId_chapter());
            chapterRepository.save(chapter_First);
        } else if (chapter.getId_chapter() == chapter.getNextChap()) {
            Chapter chapter_Last = chapterRepository.findChapterById(chapter.getPreChap());
            chapter_Last.setNextChap(chapter_Last.getId_chapter());
            chapterRepository.save(chapter_Last);
        } else {
            Chapter chapter_next = chapterRepository.findChapterById(chapter.getNextChap());
            Chapter chapter_prev = chapterRepository.findChapterById(chapter.getPreChap());
            chapter_next.setPreChap(chapter_prev.getId_chapter());
            chapter_prev.setNextChap(chapter_next.getId_chapter());
            chapterRepository.saveAll(Arrays.asList(chapter_prev, chapter_next));
        }
        bookService.deleteChapter(Long.parseLong(id_delete));
        ListBook book = bookRepository.findByID(chapter.getBook().getId_list());
        book.setCount_chapter(book.getCount_chapter() - 1);
        dirService.deleteDir(file);
        bookRepository.save(book);
        select_book(request, model);
        return "chaptermanager";
    }
    @PostMapping("/delete_page")
    public String deletePageBookByAuthor(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String id_delete = request.getParameter("id");
        Picture pagedelete = pictureRepository.findpictureByID(Long.parseLong(id_delete));
        bookService.deleteonepage(Long.parseLong(id_delete));
        selectChapter(request, model);
        String currentDirectory = System.getProperty("user.dir");
        File file = new File(currentDirectory + pagedelete.getLink(session.getAttribute("id_book").toString()));
        dirService.deleteDir(file);
        return "setupchapter";
    }



}
