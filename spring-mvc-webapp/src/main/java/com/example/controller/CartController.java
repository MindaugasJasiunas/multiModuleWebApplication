package com.example.controller;


import com.example.demo.entity.Cart;
import com.example.demo.entity.Item;
import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.CartService;
import com.example.demo.service.ItemService;
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
    private final ItemService itemService;

    public CartController(CartService cartService, UserEntityService userEntityService, ItemService itemService) {
        this.cartService = cartService;
        this.userEntityService = userEntityService;
        this.itemService = itemService;
    }

    @RequestMapping("/cart")
    public String showShoppingCartPage(@AuthenticationPrincipal UserEntity user, Model model){
        if(userEntityService.isUserExistsByUserEntityEmail(user.getUsername())){
            UserEntity userEntity= userEntityService.findUserEntityByEmail(user.getUsername()).get();

            Cart cart= cartService.createNewOrFindExistingCart(userEntity);
            //update cart items before showing
            cartService.refreshCart(cart);
            model.addAttribute("cartItems", cart.getCartItems());
            model.addAttribute("cartTotal", cartService.getCartTotalPrice(user));
        }else{
            return "redirect:/";
        }
        return "cart";
    }


    @PostMapping("/addToCart/{itemPublicId}")
    public String processCheckout(@AuthenticationPrincipal UserEntity user, @PathVariable("itemPublicId") UUID itemPublicId, @RequestParam(value = "inputQuantity", defaultValue = "0") String quantity) {
        //if item exists
        if(itemService.findItemByPublicId(itemPublicId).isPresent()){
            Item item= itemService.findItemByPublicId(itemPublicId).get();

            int itemQuantity=0;
            try{
                //server-side validation
                itemQuantity=Integer.parseInt(quantity);

                cartService.addItemToCart(user, itemPublicId, itemQuantity);
            }catch (NumberFormatException e){
                return "redirect:/item/"+itemPublicId;
            }
        }
        //return redirect to the same item with success
        return "redirect:/item/"+itemPublicId+"?added";
    }


    @RequestMapping("/checkout")
    public String showCheckoutPage(@AuthenticationPrincipal UserEntity user){
        //if there is no items in cart and user going to checkout page - redirect to main page
        if(cartService.getCartTotalAmountOfItems(user)==0){
            return "redirect:/";
        }
        //update cart items before showing
        cartService.refreshCart(cartService.createNewOrFindExistingCart(user));
        //TODO: add items from cart in model & change HTML view
        return "checkout";
    }
}
