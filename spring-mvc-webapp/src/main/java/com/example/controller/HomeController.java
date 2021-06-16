package com.example.controller;

import com.example.demo.dao.UserEntityRepository;
import com.example.demo.entity.UserEntity;
import com.example.demo.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    @Autowired
    UserEntityService service;

    @RequestMapping("/")
    public String showProductListPage() {
        return "product-list-page";
    }

    @RequestMapping("/product")
    public String showProductPage() {
        return "product-page";
    }



}

