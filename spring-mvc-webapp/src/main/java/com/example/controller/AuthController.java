package com.example.controller;

import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.UserEntityService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@Controller
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final UserEntityService userEntityService;

    public AuthController(PasswordEncoder passwordEncoder, UserEntityService userEntityService) {
        this.passwordEncoder = passwordEncoder;
        this.userEntityService = userEntityService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model){
        model.addAttribute("user", new UserEntity());
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }

    @GetMapping("/forgot")
    public String showForgotPasswordPage(){
        return "forgot-password";
    }

    @PostMapping("/register")
    public String processRegisterPage(@Valid @ModelAttribute("user") UserEntity user, BindingResult br, @RequestParam(name = "encryptedPassword", required = false) String encryptedPassword, @RequestParam(name = "inputPasswordConfirm", required = false) String inputPasswordConfirm){
//        (HttpServletRequest request)
//        request.getParameter("encryptedPassword")     //by name attr changed because ' th:field="${user.encryptedPassword}" '
//        request.getParameter("inputPasswordConfirm")  //by name

        //validate & do stuff
        if(encryptedPassword==null){
            // error propagates to bindingResult by itself
        }else if(inputPasswordConfirm==null){
            br.rejectValue("encryptedPassword", "error.user", "Confirm Password field is empty.");
        }else{
            if(!encryptedPassword.equals(inputPasswordConfirm)){
                br.rejectValue("encryptedPassword", "error.user", "Passwords doesn't match.");
            }
        }
        if(userEntityService.emailAlreadyExistsInDB(user.getEmail())){
            br.rejectValue("email", "error.user", "User with this email already exists.");
        }

        if(br.hasErrors()) {
//            br.getAllErrors().forEach(e -> System.out.println(e.toString()));
            return "register";
        }else{
            user.setEncryptedPassword(passwordEncoder.encode(user.getEncryptedPassword()));
            Optional<UserEntity> userSaved= userEntityService.saveOrUpdate(user);

            return "redirect:/?successRegistration";
        }
    }


    //(!) Trims whitespaces(leading&trailing) from fields before validation
    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        StringTrimmerEditor ste= new StringTrimmerEditor(true); //true- trim until null
        dataBinder.registerCustomEditor(String.class, ste);
    }
}
