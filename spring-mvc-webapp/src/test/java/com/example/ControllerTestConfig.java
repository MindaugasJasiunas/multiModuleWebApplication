package com.example;

import com.example.demo.service.*;
import com.example.demo.service.authentication.EmailServiceImpl;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerTestConfig {

    @Bean
    public UserEntityServiceImpl userEntityService(){
        return Mockito.mock(UserEntityServiceImpl.class);
    }

    @Bean
    public CartServiceImpl cartService(){
        return Mockito.mock(CartServiceImpl.class);
    }

    @Bean
    public ItemServiceImpl itemService(){
        return Mockito.mock(ItemServiceImpl.class);
    }

    @Bean
    public OrderServiceImpl orderService(){
        return Mockito.mock(OrderServiceImpl.class);
    }

    @Bean
    public EmailServiceImpl emailService(){
        return Mockito.mock(EmailServiceImpl.class);
    }


}
