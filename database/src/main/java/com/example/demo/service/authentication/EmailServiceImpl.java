package com.example.demo.service.authentication;

import com.example.demo.dao.authentication.AccountVerificationRepository;
import com.example.demo.entity.authentication.AccountVerification;
import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.UserEntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j

@Service
public class EmailServiceImpl implements EmailService{
    private final AccountVerificationRepository accountVerificationRepo;
    private final UserEntityService userEntityService;
    private JavaMailSender emailSender;
    private SpringTemplateEngine thymeleafTemplateEngine;

    public EmailServiceImpl(AccountVerificationRepository accountVerificationRepo, UserEntityService userEntityService, JavaMailSender emailSender, SpringTemplateEngine thymeleafTemplateEngine) {
        this.accountVerificationRepo = accountVerificationRepo;
        this.userEntityService = userEntityService;
        this.emailSender = emailSender;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    @Override
    public boolean isAlreadyAccountVerificationByUserEmail(String email){
        if(userEntityService.findUserEntityByEmail(email).isPresent()){
            UserEntity user= userEntityService.findUserEntityByEmail(email).get();
            if(accountVerificationRepo.findAccountVerificationByUserEntityPublicId(user.getPublicId()).isPresent()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteAccountVerificationByUserEntityPublicId(UUID userEntityPublicId){
        if(userEntityService.findUserEntityByPublicId(userEntityPublicId).isPresent()){
            accountVerificationRepo.deleteAccountVerificationByUserEntityPublicId(userEntityPublicId);
        }
    }

    @Override
    public Optional<AccountVerification> findAccountVerificationByVerificationCode(UUID verificationCode){
        return accountVerificationRepo.findAccountVerificationByVerificationCode(verificationCode);
    }


    @Override
    public void sendVerificationEmail(String email, boolean resetPassword){
        //make DB entry
        UserEntity userEntity= userEntityService.findUserEntityByEmail(email).get();
        AccountVerification verification=new AccountVerification(userEntity.getPublicId(), resetPassword);
        if(!isAlreadyAccountVerificationByUserEmail(email)){
            //if not already in DB- create new
            verification= accountVerificationRepo.save(verification);
        }else if(isAlreadyAccountVerificationByUserEmail(email)){
            //update DB row & send email again
            AccountVerification alreadyInDB= accountVerificationRepo.findAccountVerificationByUserEntityPublicId(userEntity.getPublicId()).get();
            alreadyInDB.setVerificationCode(verification.getVerificationCode());
            accountVerificationRepo.save(alreadyInDB);
        }
        try{
            Map<String, Object> arguments=new HashMap<>();
            arguments.put("link","http://localhost:8080/spring_mvc_webapp_war_exploded/verify/"+verification.getVerificationCode());
            sendMessageUsingThymeleafTemplate(email, "Email verification letter", arguments);
        }catch (MessagingException e){
            log.error("Error when sending email: "+e.getMessage());
        }
    }



    private void sendMessageUsingThymeleafTemplate(String to, String subject, Map< String, Object> arguments) throws MessagingException {
        Context ctx= new Context();   // import org.thymeleaf.Context
        ctx.setVariables(arguments);
        String htmlBody= thymeleafTemplateEngine.process("emails/passwordVerificationEmailForm.html", ctx);
        sendHtmlMessage(to, subject, htmlBody);
    }

    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage msg= emailSender.createMimeMessage();

        MimeMessageHelper helper= new MimeMessageHelper(msg, true); //'true'- multipart
        helper.setFrom("noreply@example.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);  // 'true'- HTML content type

        emailSender.send(msg);
    }





}
