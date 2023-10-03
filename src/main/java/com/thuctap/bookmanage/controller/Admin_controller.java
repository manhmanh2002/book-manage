package com.thuctap.bookmanage.controller;

import com.thuctap.bookmanage.entity.ListBook;
import com.thuctap.bookmanage.entity.Role;
import com.thuctap.bookmanage.entity.User;
import com.thuctap.bookmanage.repository.UserRepository;
import com.thuctap.bookmanage.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.io.File;
import java.util.List;

@AllArgsConstructor
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
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
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
        model.addAttribute("user",new User());
        if(session.getAttribute("id") == null){

            return "login";
        }
        return "Admin_book";
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
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
        User user = userService.findUser(Long.parseLong(request.getParameter("id_user")));
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
        User user = userService.findUser(Long.parseLong(request.getParameter("id_user")));
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
        String id_delete = request.getParameter("id");
        File file = new File(currentDirectory+"/data/"+id_delete);
        dirService.deleteDir(file);
        bookService.deleteBook(Long.parseLong(id_delete));
        Admin_book(request,model);
        return "Admin_book";
    }
}
