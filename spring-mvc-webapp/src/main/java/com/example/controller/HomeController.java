package com.example.controller;

import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.ItemService;
import com.example.demo.service.ItemSortingService;
import com.example.demo.service.UserEntityService;
import com.example.demo.service.authentication.JpaUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    private ItemService itemService;
    private ItemSortingService itemSortingService;
    private UserEntityService userEntityService;

    public HomeController(ItemService itemService, ItemSortingService itemSortingService, UserEntityService userEntityService) {
        this.itemService = itemService;
        this.itemSortingService = itemSortingService;
        this.userEntityService = userEntityService;
    }

    @ModelAttribute //all method calls will have same model attribute
    public void addAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email= auth.getName(); //get logged in username
        UserEntity user=userEntityService.findUserEntityByEmail(email).orElse(null);
        if(user!=null){
            model.addAttribute("userName", user.getFirstName()+" "+user.getLastName());
        }
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
    public String showShoppingCartPage(@AuthenticationPrincipal UserEntity user, Model model){
        if(userEntityService.findUserEntityByEmail(user.getUsername()).isPresent()){
            UserEntity userEntity= userEntityService.findUserEntityByEmail(user.getUsername()).get();
            //TODO: implement functionality & load cart by user
        }else{
            return "redirect:/";
        }
        //load items to shopping cart by user
        return "cart";
    }

    @RequestMapping("/admin")
    public String showAdminPage(){
        return "admin";
    }

}

