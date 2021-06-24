package com.example.controller;

import com.example.demo.entity.authentication.AccountVerification;
import com.example.demo.entity.authentication.PasswordReset;
import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.UserEntityService;
import com.example.demo.service.authentication.EmailService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Controller
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final UserEntityService userEntityService;
    private final EmailService emailService;

    public AuthController(PasswordEncoder passwordEncoder, UserEntityService userEntityService, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userEntityService = userEntityService;
        this.emailService = emailService;
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

    @PostMapping("/forgot")
    public String showForgotPasswordPage(@RequestParam(name = "email", required = false) String email){
        if(email!=null){
            if(!emailService.isAlreadyAccountVerificationByUserEmail(email)){ //if there is not already saved - proceed
                if(userEntityService.findUserEntityByEmail(email).isPresent()){
                    //make user disabled & send email
                    userEntityService.makeUserDisabledByEmail(email);
                    emailService.sendVerificationEmail(email, true);
                    return "redirect:/?successRegistration";
                }
            }else if(emailService.isAlreadyAccountVerificationByUserEmail(email)){
                //send once again
                emailService.sendVerificationEmail(email, true);
                return "redirect:/?successRegistration";
            }
        }
        return "redirect:/forgot?error";
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

        if(br.hasErrors()) {
//            br.getAllErrors().forEach(e -> System.out.println(e.toString()));
            return "register";
        }else{
            user.setEncryptedPassword(passwordEncoder.encode(user.getEncryptedPassword()));
            Optional<UserEntity> userSaved= userEntityService.saveOrUpdate(user);
            emailService.sendVerificationEmail(userSaved.get().getEmail(), false);

            return "redirect:/?successRegistration";
        }
    }


    @GetMapping("/verify/{uuid}")
    public String verify(@PathVariable("uuid") UUID uuid){
        //check if uuid in AccountVerification & based on if reset= display special form to create new password
        if(emailService.findAccountVerificationByVerificationCode(uuid).isPresent()){
            AccountVerification verification= emailService.findAccountVerificationByVerificationCode(uuid).get();
            //if reset= display special form to create new password
            if(verification.isResetPassword()){
                //display special form to reset password
                return "redirect:/verify/"+uuid+"/new";
            }else{
                //if not reset= enable user
                UUID userEntityPublicId= verification.getUserEntityPublicId();
                UserEntity userEntity= userEntityService.findUserEntityByPublicId(userEntityPublicId).get();
                userEntityService.makeUserEnabledByEmail(userEntity.getEmail());

                //delete entry
//                UserEntity userEntity=userEntityService.findUserEntityByPublicId(verification.getUserEntityPublicId()).get();
                emailService.deleteAccountVerificationByUserEntityPublicId(userEntity.getPublicId());
            }
        }
        return "redirect:/";
    }

    @GetMapping("/verify/{uuid}/new")
    public String newPasswordForm(Model model, @PathVariable("uuid") UUID uuid){
        model.addAttribute("passwordReset", new PasswordReset());
        model.addAttribute("uuid", uuid);
        return "new-password-form";
    }

    @PostMapping("/verify/{uuid}/new")
    public String processNewPasswordForm(@Valid @ModelAttribute("passwordReset") PasswordReset passwordReset, BindingResult br, @PathVariable("uuid") UUID uuid){
        if(!passwordReset.getPassword().equals(passwordReset.getRepeatPassword())){
            br.rejectValue("password", "error.password", "Passwords doesn't match");
        }
        if(br.hasErrors()){
            return "new-password-form";
        }else{
            if(emailService.findAccountVerificationByVerificationCode(uuid).isPresent()) {
                AccountVerification verification = emailService.findAccountVerificationByVerificationCode(uuid).get();
                if(userEntityService.findUserEntityByPublicId(verification.getUserEntityPublicId()).isPresent()){
                    UserEntity userEntity= userEntityService.findUserEntityByPublicId(verification.getUserEntityPublicId()).get();
                    //save new password in
                    userEntity.setEncryptedPassword(passwordEncoder.encode(passwordReset.getPassword()));
                    userEntityService.saveOrUpdate(userEntity);

                    //enable user
                    userEntityService.makeUserEnabledByEmail(userEntity.getEmail());

                    //delete entry
                    emailService.deleteAccountVerificationByUserEntityPublicId(userEntity.getPublicId());
                }
            }
        }
        return "redirect:/";
    }


    //(!) Trims whitespaces(leading&trailing) from fields before validation
    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        StringTrimmerEditor ste= new StringTrimmerEditor(true); //true- trim until null
        dataBinder.registerCustomEditor(String.class, ste);
    }
}
