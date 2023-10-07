package com.thuctap.bookmanage.controller;

import com.thuctap.bookmanage.dao.request.SignupRequest;
import com.thuctap.bookmanage.entity.Enable;
import com.thuctap.bookmanage.entity.Type;
import com.thuctap.bookmanage.entity.User;
import com.thuctap.bookmanage.config.FileUploadUtil;
import com.thuctap.bookmanage.repository.TypeRepository;
import com.thuctap.bookmanage.repository.UserRepository;
import com.thuctap.bookmanage.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
public class User_Controller {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private TypeService typeService;

    @RequestMapping("/homepage")
    public String HomePage(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        if(session.getAttribute("id") == null){
            model.addAttribute("message","You need login to user function!!!");
            model.addAttribute("user",new User());
            return "login";
        }
        User user = userService.findUser(Long.parseLong(session.getAttribute("id").toString()));
        System.out.println(user);
        System.out.println(session.getAttribute("id"));
        model.addAttribute("user",user);
        model.addAttribute("id",session.getAttribute("id"));
        return "homepage";
    }
    @GetMapping("/")
    public String notLogin(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        model.addAttribute("list_book",bookService.showAllBook());
        List<Type> list_type = typeRepository.showAllCategory();
        if(list_type.isEmpty()){
            typeService.saveType();
        }
        model.addAttribute("categories",list_type);
        if(session.getAttribute("id")!=null){
            return HomePage(request,model);
        }else {
            return "index";
        }
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/signup")
    public String login(Model model, HttpServletRequest request, Principal principal) {
        HttpSession session = request.getSession();
        User oauthUser = userRepository.findByEmail(principal.getName());
        if(oauthUser.getEnable().equals(Enable.TRUE)) {
                model.addAttribute("id", String.valueOf(oauthUser.getId_user()));
                session.setAttribute("id", String.valueOf(oauthUser.getId_user()));
                model.addAttribute("user", oauthUser);
                return "homepage";
        }else {
            model.addAttribute("message", "The account is disable!");
            return "login";
        }
    }
    @RequestMapping(value = "/change_password")
    public String changePassword(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        String x = session.getAttribute("id").toString();
        model.addAttribute("id", String.valueOf(x));
        return "change_password";
    }
    @PostMapping(value = "/change_your_password")
    public String processChangePassword(Model model,HttpServletRequest request){
        HttpSession session = request.getSession();
        String x = session.getAttribute("id").toString();
        User user = userService.findUser(Long.parseLong(x));
        String newPass = request.getParameter("password");
        String oldPass = request.getParameter("old_password");
        if(userService.oldPasswordValid(user,oldPass)){
            userService.changePass(user.getEmail(), request);
            user.setNew_pass(newPass);
            userRepository.save(user);
            model.addAttribute("message","Please check mail to confirm change password!!!");
        }else {
            model.addAttribute("message","Wrong pass");
        }
        return "Close";
    }
    @GetMapping("change_passwordSuccess")
    public String ChangePasswordSuccess(@Param(value = "token") String token, Model model) throws UserNotFoundException {
        User user = userService.get(token);
        if(user != null){
            userService.updatePassword(user,user.getNew_pass());

            userRepository.save(user);
        }else{
            model.addAttribute("title","Reset your password!");
            model.addAttribute("message","Invalid token");
        }
        return "change_passwordSuccess";
    }
    @RequestMapping("/register")
    public String register(){
        return "register";
    }
    @PostMapping(value = "/registration",consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String success(@ModelAttribute("user") SignupRequest user, HttpServletRequest request, BindingResult result, Model model) {
        if(result.hasErrors()){
            model.addAttribute("user",new SignupRequest());
            return "register";
        }
        if(Objects.nonNull(userService.checkEmail(user.getEmail()))) {
            return "redirect:/register?error_email";
        } else {
            authenticationService.signUp(user,request);
            return "Close";
        }

    }
    @GetMapping("/confirm_account")
    public String confirmRegistration(@Param(value = "token") String token, Model model){
        User user = userService.get(token);
        if(Objects.nonNull(user)){
            user.setEnable(Enable.TRUE);
            user.setToken(null);
            userRepository.save(user);
        }
        return "confirm_account";
    }
    @GetMapping("/forgot_password")
    public String ForgotPasswordForm(Model model){
        model.addAttribute("pageTitle","ForgotPassword");
        return "Forgot_Password_Form";
    }

    @PostMapping("/forgot_password")
    public String processForgotPasswordForm(@Valid @ModelAttribute("user") User user,HttpServletRequest request, Model model) throws UserNotFoundException {
        String email = request.getParameter("email");
        if(!Objects.nonNull(userService.checkEmail(email))){
            model.addAttribute("message","Email is incorrect!");
            return "Forgot_Password_Form";
        }
        userService.forgotPass(email,request);
        model.addAttribute("message","Please check mail to confirm change password!!!");

        return "Close";
    }
    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model){
        User user = userService.get(token);
        if(!Objects.nonNull(user)){
            model.addAttribute("title","Reset your password!");
            model.addAttribute("message","Invalid token");
            return "message";
        }
        model.addAttribute("token",token);
        model.addAttribute("pageTitle","Reset your password!");
        return "reset_password_form";
    }
    @PostMapping("reset_password")
    public String processResetPassword(HttpServletRequest request, Model model){
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        User user = userService.get(token);
        if(!Objects.nonNull(user)){
            model.addAttribute("title","Reset your password!");
            model.addAttribute("message","Invalid token");
        }else {
            userService.updatePassword(user,password);
            return "change_passwordSuccess";
        }
        return "message";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response)
    {
        SecurityContextHolder.clearContext();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return "/index";
    }

    @RequestMapping(value="uploadphotos")
    public String pageUpload(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        String id = session.getAttribute("id").toString();
        User user = userService.findUser(Long.parseLong(id));
        model.addAttribute("user", user);
        return "uploadphotos";
    }

    @PostMapping("/savepicture")
    public String saveFiles (HttpServletRequest request, @RequestParam("image") MultipartFile multipartFile, Model model) throws IOException {
        HttpSession session = request.getSession();

        if(session.getAttribute("id") == null){
            return HomePage(request,model);
        }
        Long id = Long.parseLong(session.getAttribute("id").toString());
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        User user = userService.findUser(id);
        model.addAttribute("user",user);
        if(!fileName.isEmpty()){
            user.setPhoto(fileName);
            userRepository.save(user);
            String uploadDir = "user-photos/" + user.getId_user();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            model.addAttribute("message", "Change avatar success!!!");
            return "homepage";
        } else {
            model.addAttribute("message", "Avatar is empty!!! Couldn't change:( !");
            return "uploadphotos";
        }
    }

}