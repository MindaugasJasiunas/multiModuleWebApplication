package com.example.demo.service.authentication;

import com.example.demo.dao.authentication.AccountVerificationRepository;
import com.example.demo.entity.Order;
import com.example.demo.entity.authentication.AccountVerification;
import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.UserEntityService;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Or;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;

@PropertySource("classpath:mail.properties")
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {
    @Mock
    private AccountVerificationRepository accountVerificationRepo;
    @Mock
    private UserEntityService userEntityService;
    @Mock
    private JavaMailSender emailSender;
    @Mock
    private SpringTemplateEngine thymeleafTemplateEngine;
    @InjectMocks
    EmailServiceImpl emailService;


    @DisplayName("isAlreadyAccountVerificationByUserEmail() - already is")
    @Test
    void isAlreadyAccountVerificationByUserEmail() {
        //given
        UserEntity userEntity=new UserEntity();
        Mockito.when(userEntityService.findUserEntityByEmail(anyString())).thenReturn(Optional.of(userEntity));
        AccountVerification accountVerification=new AccountVerification();
        Mockito.when(accountVerificationRepo.findAccountVerificationByUserEntityPublicId(any(UUID.class))).thenReturn(Optional.of(accountVerification));

        //when
        boolean result= emailService.isAlreadyAccountVerificationByUserEmail("");

        //then
        assertTrue(result);
        Mockito.verify(userEntityService, Mockito.times(2)).findUserEntityByEmail(anyString());
        Mockito.verify(accountVerificationRepo, Mockito.times(1)).findAccountVerificationByUserEntityPublicId(any(UUID.class));
    }

    @DisplayName("isAlreadyAccountVerificationByUserEmail() - there is no AccountVerification")
    @Test
    void isAlreadyAccountVerificationByUserEmail_noAccountVerificationByEmail() {
        //given
        UserEntity userEntity=new UserEntity();
        Mockito.when(userEntityService.findUserEntityByEmail(anyString())).thenReturn(Optional.of(userEntity));
        Mockito.when(accountVerificationRepo.findAccountVerificationByUserEntityPublicId(any(UUID.class))).thenReturn(Optional.empty());

        //when
        boolean result= emailService.isAlreadyAccountVerificationByUserEmail("");

        //then
        assertFalse(result);
        Mockito.verify(userEntityService, Mockito.times(2)).findUserEntityByEmail(anyString());
        Mockito.verify(accountVerificationRepo, Mockito.times(1)).findAccountVerificationByUserEntityPublicId(any(UUID.class));
    }

    @DisplayName("deleteAccountVerificationByUserEntityPublicId() - user exist")
    @Test
    void deleteAccountVerificationByUserEntityPublicId() {
        //given
        UserEntity userEntity=new UserEntity();
        userEntity.setPublicId(UUID.randomUUID());
        Mockito.when(userEntityService.findUserEntityByPublicId(any(UUID.class))).thenReturn(Optional.of(userEntity));
        Mockito.doNothing().when(accountVerificationRepo).deleteAccountVerificationByUserEntityPublicId(any(UUID.class));

        //when
        emailService.deleteAccountVerificationByUserEntityPublicId(UUID.randomUUID());

        //then
        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByPublicId(any(UUID.class));
        Mockito.verify(accountVerificationRepo, Mockito.times(1)).deleteAccountVerificationByUserEntityPublicId(any(UUID.class));
    }

    @DisplayName("deleteAccountVerificationByUserEntityPublicId() - user doesn't exist")
    @Test
    void deleteAccountVerificationByUserEntityPublicId_userNotFound() {
        //given
        Mockito.when(userEntityService.findUserEntityByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        //when
        emailService.deleteAccountVerificationByUserEntityPublicId(UUID.randomUUID());

        //then
        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByPublicId(any(UUID.class));
        Mockito.verify(accountVerificationRepo, Mockito.never()).deleteAccountVerificationByUserEntityPublicId(any(UUID.class));
    }

    @Test
    void findAccountVerificationByVerificationCode() {
        //given
        AccountVerification accountVerification=new AccountVerification();
        Mockito.when(accountVerificationRepo.findAccountVerificationByVerificationCode(any(UUID.class))).thenReturn(Optional.of(accountVerification));

        //when
        Optional<AccountVerification> result= emailService.findAccountVerificationByVerificationCode(UUID.randomUUID());

        //then
        assertTrue(result.isPresent());
        assertEquals(accountVerification, result.get());
    }

    @DisplayName("findAccountVerificationByVerificationCode() - no code found")
    @Test
    void findAccountVerificationByVerificationCode_noCodeFound() {
        //given
        Mockito.when(accountVerificationRepo.findAccountVerificationByVerificationCode(any(UUID.class))).thenReturn(Optional.empty());

        //when
        Optional<AccountVerification> result= emailService.findAccountVerificationByVerificationCode(UUID.randomUUID());

        //then
        assertTrue(result.isEmpty());
    }

    @DisplayName("sendVerificationEmail() - new registration OR reset password totally equal code.")
    @Test
    void sendVerificationEmail() throws Exception {
        //given
        UserEntity userEntity=new UserEntity();
        AccountVerification accountVerification=new AccountVerification();

        Mockito.when(userEntityService.findUserEntityByEmail(anyString())).thenReturn(Optional.of(userEntity));

        final ArgumentCaptor<AccountVerification> accountVerificationArgumentCaptor= ArgumentCaptor.forClass(AccountVerification.class);
        Mockito.when(accountVerificationRepo.save(accountVerificationArgumentCaptor.capture())).thenReturn(accountVerification);


        String emailSubject="tempSubject";
        emailService.setMailVerificationTitle(emailSubject);

        String emailContent= "htmlBody";
        Mockito.when(thymeleafTemplateEngine.process(nullable(String.class), any(Context.class))).thenReturn(emailContent);

        MimeMessage mimeMessage= new JavaMailSenderImpl().createMimeMessage();
        Mockito.when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        String emailFrom="tempMailSender";
        emailService.setMailSender(emailFrom);

        final ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor= ArgumentCaptor.forClass(MimeMessage.class);
        Mockito.doNothing().when(emailSender).send(mimeMessageArgumentCaptor.capture());

        String emailTo="testEmail@example.com";
        //when
        emailService.sendVerificationEmail(emailTo, false);

        //then
        assertFalse(accountVerificationArgumentCaptor.getValue().isResetPassword());
        MimeMessageParser parser = new MimeMessageParser(mimeMessageArgumentCaptor.getValue());  // commons-email dependency
        parser.parse();
        assertEquals(emailSubject, parser.getSubject());
        assertEquals(emailFrom, parser.getFrom());
        assertEquals(1, parser.getTo().size());
        assertEquals(emailTo, parser.getTo().get(0).toString());
        assertEquals(emailContent, parser.getHtmlContent().toString());

        Mockito.verify(userEntityService, Mockito.times(4)).findUserEntityByEmail(anyString());
        Mockito.verify(accountVerificationRepo, Mockito.times(1)).save(any(AccountVerification.class));
        Mockito.verify(thymeleafTemplateEngine, Mockito.times(1)).process(nullable(String.class), any(Context.class));
        Mockito.verify(emailSender, Mockito.times(1)).createMimeMessage();
    }

    @DisplayName("sendVerificationEmail() - user by email not found")
    @Test
    void sendVerificationEmail_noUserByEmail() {
        //given
        Mockito.when(userEntityService.findUserEntityByEmail(anyString())).thenReturn(Optional.empty());

        //when
        emailService.sendVerificationEmail("exampleEmail", false);

        //then
        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByEmail(anyString());
        Mockito.verify(accountVerificationRepo, Mockito.never()).save(any(AccountVerification.class));
        Mockito.verify(thymeleafTemplateEngine, Mockito.never()).process(nullable(String.class), any(Context.class));
        Mockito.verify(emailSender, Mockito.never()).createMimeMessage();
    }

    @DisplayName("sendVerificationEmail() - account verification record already in DB")
    @Test
    void sendVerificationEmail_verificationRecordAlreadyInDB() throws Exception {
        //given
        UserEntity userEntity=new UserEntity();
        AccountVerification accountVerification=new AccountVerification();
        UUID accountVerificationCode=UUID.randomUUID();
        accountVerification.setVerificationCode(accountVerificationCode);

        Mockito.when(userEntityService.findUserEntityByEmail(anyString())).thenReturn(Optional.of(userEntity));

        Mockito.when(accountVerificationRepo.findAccountVerificationByUserEntityPublicId(any(UUID.class))).thenReturn(Optional.of(accountVerification)); // record already exists in DB

        final ArgumentCaptor<AccountVerification> accountVerificationArgumentCaptor= ArgumentCaptor.forClass(AccountVerification.class);
        Mockito.when(accountVerificationRepo.save(accountVerificationArgumentCaptor.capture())).thenReturn(accountVerification);


        String emailSubject="tempSubject";
        emailService.setMailVerificationTitle(emailSubject);

        String emailContent= "htmlBody";
        Mockito.when(thymeleafTemplateEngine.process(nullable(String.class), any(Context.class))).thenReturn(emailContent);

        MimeMessage mimeMessage= new JavaMailSenderImpl().createMimeMessage();
        Mockito.when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        String emailFrom="tempMailSender";
        emailService.setMailSender(emailFrom);

        final ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor= ArgumentCaptor.forClass(MimeMessage.class);
        Mockito.doNothing().when(emailSender).send(mimeMessageArgumentCaptor.capture());

        String emailTo="testEmail@example.com";
        //when
        emailService.sendVerificationEmail(emailTo, false);

        //then
        assertNotEquals(accountVerificationCode, accountVerificationArgumentCaptor.getValue().getVerificationCode()); // code is changed
        MimeMessageParser parser = new MimeMessageParser(mimeMessageArgumentCaptor.getValue());  // commons-email dependency
        parser.parse();
        assertEquals(emailSubject, parser.getSubject());
        assertEquals(emailFrom, parser.getFrom());
        assertEquals(1, parser.getTo().size());
        assertEquals(emailTo, parser.getTo().get(0).toString());
        assertEquals(emailContent, parser.getHtmlContent().toString());

        Mockito.verify(userEntityService, Mockito.times(6)).findUserEntityByEmail(anyString());
        Mockito.verify(accountVerificationRepo, Mockito.times(3)).findAccountVerificationByUserEntityPublicId(any(UUID.class));
        Mockito.verify(accountVerificationRepo, Mockito.times(1)).save(any(AccountVerification.class));
        Mockito.verify(thymeleafTemplateEngine, Mockito.times(1)).process(nullable(String.class), any(Context.class));
        Mockito.verify(emailSender, Mockito.times(1)).createMimeMessage();
    }

    @Test
    void sendOrderConfirmationEmail() throws Exception {
        //given
        UserEntity userEntity=new UserEntity();
        Mockito.when(userEntityService.findUserEntityByEmail(anyString())).thenReturn(Optional.of(userEntity));

        String mailOrderTitle="tempTitle";
        emailService.setMailOrderTitle(mailOrderTitle);


        String emailContent= "htmlBody";
        emailService.setMailOrderTemplate("emails/placedOrderEmailForm.html");
        Mockito.when(thymeleafTemplateEngine.process(any(String.class), any(Context.class))).thenReturn(emailContent);

        MimeMessage mimeMessage= new JavaMailSenderImpl().createMimeMessage();
        Mockito.when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        String emailFrom="tempMailSender";
        emailService.setMailSender(emailFrom);

        final ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor= ArgumentCaptor.forClass(MimeMessage.class);
        Mockito.doNothing().when(emailSender).send(mimeMessageArgumentCaptor.capture());

        String emailTo="testEmail@example.com";
        Order order=new Order();
        MonetaryAmount amountTotal= Monetary.getDefaultAmountFactory().setCurrency(Monetary.getCurrency("EUR")).setNumber( 123.45 ).create();
        //when
        emailService.sendOrderConfirmationEmail(emailTo, order, amountTotal);

        //then
        MimeMessageParser parser = new MimeMessageParser(mimeMessageArgumentCaptor.getValue());  // commons-email dependency
        parser.parse();
        assertEquals(mailOrderTitle, parser.getSubject());
        assertEquals(emailFrom, parser.getFrom());
        assertEquals(1, parser.getTo().size());
        assertEquals(emailTo, parser.getTo().get(0).toString());
        assertEquals(emailContent, parser.getHtmlContent().toString());

        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByEmail(anyString());
        Mockito.verify(thymeleafTemplateEngine, Mockito.times(1)).process(nullable(String.class), any(Context.class));
        Mockito.verify(emailSender, Mockito.times(1)).createMimeMessage();
        Mockito.verify(emailSender, Mockito.times(1)).send(any(MimeMessage.class));
    }
}