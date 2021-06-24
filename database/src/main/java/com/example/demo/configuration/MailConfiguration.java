package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;

@PropertySource("classpath:mail.properties")
@Configuration
public class MailConfiguration {
    private final Environment env;

    public MailConfiguration(Environment env) {
        this.env = env;
    }

    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender= new JavaMailSenderImpl();
        mailSender.setHost( env.getProperty("mail.host") );
        mailSender.setPort( Integer.parseInt( Objects.requireNonNull( env.getProperty("mail.port") ) ) );				           //Config on local MailDev
        mailSender.setUsername( env.getProperty("mail.username") );
        mailSender.setPassword( env.getProperty("mail.password") );

        Properties props= mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", env.getProperty("mail.transport.protocol") );
        props.put("mail.smtp.ssl.trust", env.getProperty("mail.smtp.ssl.trust") );
        props.put("mail.smtp.auth", env.getProperty("mail.smtp.auth") );
        props.put("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable") );
        props.put("mail.smtp.connectiontimeout", Integer.parseInt( Objects.requireNonNull(env.getProperty("mail.smtp.connectiontimeout")) ) );
        props.put("mail.smtp.timeout", Integer.parseInt( Objects.requireNonNull(env.getProperty("mail.smtp.timeout")) ) );
        props.put("mail.smtp.writetimeout", Integer.parseInt( Objects.requireNonNull(env.getProperty("mail.smtp.writetimeout")) ) );
        //props.put("mail.debug","true");
        return mailSender;
    }
}
