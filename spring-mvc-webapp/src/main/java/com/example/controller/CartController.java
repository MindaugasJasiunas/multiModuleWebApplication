package com.example.controller;


import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.UserEntityService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CartController {
    private final UserEntityService userEntityService;

    public CartController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
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

}
