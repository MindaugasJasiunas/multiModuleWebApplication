package com.example.controller;


import com.example.demo.entity.Cart;
import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.CartService;
import com.example.demo.service.UserEntityService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class CartController {
    private final CartService cartService;
    private final UserEntityService userEntityService;

    public CartController(CartService cartService, UserEntityService userEntityService) {
        this.cartService = cartService;
        this.userEntityService = userEntityService;
    }

    @RequestMapping("/cart")
    public String showShoppingCartPage(@AuthenticationPrincipal UserEntity user, Model model){
        if(userEntityService.isUserExistsByUserEntityEmail(user.getUsername())){
            UserEntity userEntity= userEntityService.findUserEntityByEmail(user.getUsername()).get();

            //TODO: implement functionality & load cart by user
            Cart cart= cartService.createNewOrFindExistingCart(userEntity);
            model.addAttribute("cartItems", cart.getCartItems());

        }else{
            return "redirect:/";
        }
        //load items to shopping cart by user
        return "cart";
    }

    @PostMapping("/addToCart/{itemPublicId}")
    public String processCheckout(@AuthenticationPrincipal UserEntity user, @PathVariable("itemPublicId") UUID itemPublicId, @RequestParam(value = "inputQuantity", defaultValue = "0") String quantity) {
        //return redirect to the same item
        return "redirect:/item/"+itemPublicId+"?added";
    }

}
