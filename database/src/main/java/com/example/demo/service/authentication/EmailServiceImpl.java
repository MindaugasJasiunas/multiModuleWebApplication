package com.example.demo.service.authentication;

import com.example.demo.dao.authentication.AccountVerificationRepository;
import com.example.demo.entity.Order;
import com.example.demo.entity.authentication.AccountVerification;
import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.UserEntityService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.money.MonetaryAmount;
import java.util.*;

@Slf4j
@Setter

@PropertySource("classpath:mail.properties")
@Service
public class EmailServiceImpl implements EmailService{
    private final AccountVerificationRepository accountVerificationRepo;
    private final UserEntityService userEntityService;
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;

    @Value("${mail.verification.template}")
    private String mailVerificationTemplate;
    @Value("${mail.order.template}")
    private String mailOrderTemplate;
    @Value("${mail.verification.title}")
    private String mailVerificationTitle;
    @Value("${mail.order.title}")
    private String mailOrderTitle;
    @Value("${mail.sender}")
    private String mailSender;
    @Value("${mail.baselink}")
    private String mailBaseLink;

    public EmailServiceImpl(AccountVerificationRepository accountVerificationRepo, UserEntityService userEntityService, JavaMailSender emailSender, SpringTemplateEngine thymeleafTemplateEngine) {
        this.accountVerificationRepo = accountVerificationRepo;
        this.userEntityService = userEntityService;
        this.emailSender = emailSender;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    /**
     * Method checks if there is account verification already in DB
     * Method used to check before creating new or updating.
     * @param  email  UserEntity email
     * @return        is record already in DB
    **/
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
        if(userEntityService.findUserEntityByEmail(email).isEmpty() || email==null){
            return;
        }
        //make DB entry
        UserEntity userEntity= userEntityService.findUserEntityByEmail(email).get();
        AccountVerification verification=new AccountVerification(userEntity.getPublicId(), resetPassword);
        if(!isAlreadyAccountVerificationByUserEmail(email)){
            //if not already in DB- create new
            verification= accountVerificationRepo.save(verification);
        }else if(isAlreadyAccountVerificationByUserEmail(email)){
            //update DB row & send email again
            AccountVerification alreadyInDB= accountVerificationRepo.findAccountVerificationByUserEntityPublicId(userEntity.getPublicId()).get();
            alreadyInDB.setVerificationCode(verification.getVerificationCode()); //update verification code
            accountVerificationRepo.save(alreadyInDB);
        }
        try{
            Map<String, Object> arguments=new HashMap<>();
            arguments.put("link", mailBaseLink + verification.getVerificationCode());
            sendConfirmationMessageUsingThymeleafTemplate(email, mailVerificationTitle, arguments);
        }catch (MessagingException e){
            log.error("Error when sending email: "+e.getMessage());
        }
    }


    @Override
    public void sendOrderConfirmationEmail(String email, Order order, MonetaryAmount totalPrice){
        if(userEntityService.findUserEntityByEmail(email).isEmpty()){
            return;
        }
        try{
            Map<String, Object> arguments=new HashMap<>();
            arguments.put("order", order);
            arguments.put("totalPrice", totalPrice);
            sendOrderMessageUsingThymeleafTemplate(email, mailOrderTitle, arguments);
        }catch (MessagingException e){
            log.error("Error when sending email: "+e.getMessage());
        }
    }


    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage msg= emailSender.createMimeMessage();
        if(to==null || subject ==null || htmlBody==null){
            return;
        }

        MimeMessageHelper helper= new MimeMessageHelper(msg, true); //'true'- multipart
        helper.setFrom(mailSender);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);  // 'true'- HTML content type

        emailSender.send(msg);
    }



    private void sendOrderMessageUsingThymeleafTemplate(String to, String subject, Map< String, Object> arguments) throws MessagingException {
        Context ctx= new Context();   // import org.thymeleaf.Context
        ctx.setVariables(arguments);
        String htmlBody= thymeleafTemplateEngine.process(mailOrderTemplate, ctx);
        sendHtmlMessage(to, subject, htmlBody);
    }


    private void sendConfirmationMessageUsingThymeleafTemplate(String to, String subject, Map< String, Object> arguments) throws MessagingException {
        Context ctx= new Context();   // import org.thymeleaf.Context
        ctx.setVariables(arguments);
        String htmlBody= thymeleafTemplateEngine.process(mailVerificationTemplate, ctx);
        sendHtmlMessage(to, subject, htmlBody);
    }



}
