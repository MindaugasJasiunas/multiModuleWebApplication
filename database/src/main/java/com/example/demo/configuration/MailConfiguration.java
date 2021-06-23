package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfiguration {

    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender= new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(1025);				           //Config on local MailDev
        mailSender.setUsername("hello");
        mailSender.setPassword("hello");

        Properties props= mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol","smtp");
        props.put("mail.smtp.ssl.trust","*");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.connectiontimeout",5000);
        props.put("mail.smtp.timeout",3000);
        props.put("mail.smtp.writetimeout",5000);
        //props.put("mail.debug","true");
        return mailSender;
    }
}
