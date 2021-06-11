package com.example;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class MySpringMvcDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    //replaces web.xml dispatcher servlet configuration

    //makes sure your code is automatically detected
    //makes sure your code is used to initialize servlet container

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null; //new Class[0];
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{MyAppConfiguration.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
