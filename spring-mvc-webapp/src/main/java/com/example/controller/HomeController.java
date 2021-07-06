package com.example.controller;

import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.ItemService;
import com.example.demo.service.ItemSortingService;
import com.example.demo.service.UserEntityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


@PropertySource("classpath:app.properties")
@Controller
public class HomeController {
    private final ItemService itemService;
    private final ItemSortingService itemSortingService;
    private final UserEntityService userEntityService;
    @Value("${context.path:localhost:8080}")
    private String webpageContextPath;

    public HomeController(ItemService itemService, ItemSortingService itemSortingService, UserEntityService userEntityService) {
        this.itemService = itemService;
        this.itemSortingService = itemSortingService;
        this.userEntityService = userEntityService;
    }

    @ModelAttribute //all method calls will have same model attribute
    public void addAttributes(Model model) {
        if(SecurityContextHolder.getContext().getAuthentication() != null){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email= auth.getName(); //get logged in username
            UserEntity user=userEntityService.findUserEntityByEmail(email).orElse(null);
            if(user!=null){
                model.addAttribute("userName", user.getFirstName()+" "+user.getLastName());
            }
        }
    }

    @RequestMapping("/")
    public String showProductListPage(Model model) {
        model.addAttribute("genderList", itemSortingService.getGenders());
        model.addAttribute("categoryList", itemSortingService.getCategories());
        model.addAttribute("sizeList", itemSortingService.getSizes());
        model.addAttribute("pageCount", itemService.pageCount(8));
        model.addAttribute("contextPath", webpageContextPath); //added if not null
        return "product-list-page";
    }

    @RequestMapping("/admin")
    public String showAdminPage(){
        return "adminpanel";
    }

}

