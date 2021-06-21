package com.example.controller;

import com.example.demo.dao.UserEntityRepository;
import com.example.demo.entity.UserEntity;
import com.example.demo.service.ItemService;
import com.example.demo.service.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    private ItemService itemService;

    public HomeController(ItemService itemService) {
        this.itemService = itemService;
    }

    @RequestMapping("/")
    public String showProductListPage(Model model) {
        model.addAttribute("pageCount", itemService.pageCount(8));
        return "product-list-page";
    }

    @RequestMapping("/product")
    public String showProductPage() {
        return "product-page";
    }

    @RequestMapping("/checkout")
    public String showCheckoutPage(){
        return "checkout";
    }

    @RequestMapping("/cart")
    public String showShoppingCartPage(){
        return "cart";
    }

    @RequestMapping("/admin")
    public String showAdminPage(){
        return "admin";
    }

    @RequestMapping("/register")
    public String showRegisterPage(){
        return "register";
    }

    @RequestMapping("/login")
    public String showLoginPage(){
        return "login";
    }

    @RequestMapping("/forgot")
    public String showForgotPasswordPage(){
        return "forgot-password";
    }


}

