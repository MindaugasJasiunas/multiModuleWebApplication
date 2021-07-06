package com.example.controller;

import com.example.configuration.MyAppConfiguration;
import com.example.configuration.MySpringMvcDispatcherServletInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration(value = "src/main/web") //default :"src/main/webapp"
@ContextConfiguration(classes = {MyAppConfiguration.class, MySpringMvcDispatcherServletInitializer.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Disabled
    @Test
    void showRegisterPage() throws Exception {
        mockMvc
            .perform(get("/"))
            .andExpect(status().isOk());
//        org.springframework.web.util.NestedServletException: Request processing failed; nested exception is org.thymeleaf.exceptions.TemplateInputException: An error happened during template parsing (template: "ServletContext resource [/view/product-list-page.html]")
//        Caused by: java.io.FileNotFoundException: Could not open ServletContext resource [/view/product-list-page.html]
    }
}