package com.example.controller;

import com.example.demo.service.ItemService;
import com.example.demo.service.ItemSortingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    private ItemService itemService;
    private ItemSortingService itemSortingService;

    public HomeController(ItemService itemService, ItemSortingService itemSortingService) {
        this.itemService = itemService;
        this.itemSortingService = itemSortingService;
    }

    @RequestMapping("/")
    public String showProductListPage(Model model) {
        model.addAttribute("genderList", itemSortingService.getGenders());
        model.addAttribute("categoryList", itemSortingService.getCategories());
        model.addAttribute("sizeList", itemSortingService.getSizes());
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

