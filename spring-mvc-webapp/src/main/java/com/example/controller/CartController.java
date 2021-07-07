package com.example.controller;


import com.example.demo.UtilClass;
import com.example.demo.entity.*;
import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.CartService;
import com.example.demo.service.ItemService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserEntityService;
import com.example.demo.service.authentication.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@PropertySource("classpath:app.properties")
@Controller
public class CartController {
    private final CartService cartService;
    private final UserEntityService userEntityService;
    private final ItemService itemService;
    private final OrderService orderService;
    private final EmailService emailService;

    @Value("${context.path}")
    private String webpageContextPath;

    public CartController(CartService cartService, UserEntityService userEntityService, ItemService itemService, OrderService orderService, EmailService emailService) {
        this.cartService = cartService;
        this.userEntityService = userEntityService;
        this.itemService = itemService;
        this.orderService = orderService;
        this.emailService = emailService;
    }

    @RequestMapping("/cart")
    public String showShoppingCartPage(@AuthenticationPrincipal User user, Model model){
        if(userEntityService.isUserExistsByUserEntityEmail(user.getUsername())){
            UserEntity userEntity= userEntityService.findUserEntityByEmail(user.getUsername()).get();

            Cart cart= cartService.createNewOrFindExistingCart(userEntity);
            //update cart items before showing
            cartService.refreshCart(cart);
            model.addAttribute("cartItems", cart.getCartItems());
            model.addAttribute("cartTotal", cartService.getCartTotalPrice(userEntity));
        }else{
            return "redirect:/";
        }
        return "cart";
    }


    @PostMapping("/addToCart/{itemPublicId}")
    public String processAddToCart(@AuthenticationPrincipal User user, @PathVariable("itemPublicId") UUID itemPublicId, @RequestParam(value = "inputQuantity", defaultValue = "0") String quantity) {
        //if item exists
        if(itemService.findItemByPublicId(itemPublicId).isPresent()){
            if(userEntityService.findUserEntityByEmail(user.getUsername()).isPresent()) {
                UserEntity userEntity = userEntityService.findUserEntityByEmail(user.getUsername()).get();
                Item item= itemService.findItemByPublicId(itemPublicId).get();

                int itemQuantity = 0;
                try {
                    //server-side validation
                    itemQuantity = Integer.parseInt(quantity);

                    cartService.addItemToCart(userEntity, itemPublicId, itemQuantity);
                } catch (NumberFormatException e) {
                    return "redirect:/item/" + itemPublicId;
                }
            }
        }
        //return redirect to the same item with success
        return "redirect:/item/"+itemPublicId+"?added";
    }


    @GetMapping("/checkout")
    public String showCheckoutPage(@AuthenticationPrincipal User user, Model model){
        if(userEntityService.isUserExistsByUserEntityEmail(user.getUsername())) {
            UserEntity userEntity= userEntityService.findUserEntityByEmail(user.getUsername()).get();

            //if there is no items in cart and user going to checkout page - redirect to main page
            if (cartService.getCartTotalAmountOfItems(userEntity) == 0) {
                return "redirect:/";
            }
            Cart userCart = cartService.createNewOrFindExistingCart(userEntity);
            //update cart items before showing
            cartService.refreshCart(userCart);

            model.addAttribute("cartItems", userCart.getCartItems());
            model.addAttribute("cartTotal", cartService.getCartTotalPrice(userEntity));
            model.addAttribute("user", user);
            model.addAttribute("totalNumberOfItems", cartService.getCartTotalAmountOfItems(userEntity));
            model.addAttribute("countries", UtilClass.getCountries());
            model.addAttribute("states", UtilClass.getStatesByCountry("Lithuania"));
            model.addAttribute("contextPath", webpageContextPath);

            Order order=new Order();
            order.setFirstName(userEntity.getFirstName());
            order.setLastName(userEntity.getLastName());
            model.addAttribute("order", order);

            return "checkout";
        }else{
            return "redirect:/";
        }
    }

    @PostMapping("/checkout")
    public String processCheckout(@Valid @ModelAttribute("order") Order order, BindingResult br, @AuthenticationPrincipal User user, Model model, HttpServletRequest request){
        if(userEntityService.isUserExistsByUserEntityEmail(user.getUsername())) {
            UserEntity userEntity = userEntityService.findUserEntityByEmail(user.getUsername()).get();
            //check if card data is valid
            br = cartService.checkBankingInfo(order, br);

            // *** send card info to bank ***
            //br=bankingService.validateAndCheckout(order, br);

            //if has form or banking card errors -return to the same page with errors
            if (br.hasErrors()) {
                Cart userCart = cartService.createNewOrFindExistingCart(userEntity);
                //update cart items before showing
                cartService.refreshCart(userCart);
                model.addAttribute("cartItems", userCart.getCartItems());
                model.addAttribute("cartTotal", cartService.getCartTotalPrice(userEntity));
                model.addAttribute("user", userEntity);
                model.addAttribute("totalNumberOfItems", cartService.getCartTotalAmountOfItems(userEntity));
                model.addAttribute("order", order);
                model.addAttribute("countries", UtilClass.getCountries());
                model.addAttribute("states", UtilClass.getStatesByCountry("Lithuania"));
                model.addAttribute("contextPath", webpageContextPath);
                return "checkout";
            }
            //no errors
            Cart cart = cartService.createNewOrFindExistingCart(userEntity);
            //refresh cart one last time
            cartService.refreshCart(cart);

            //copy CartItems to OrderItems
            for (CartItem cartItem : cart.getCartItems()) {
                OrderItem temp = new OrderItem();
                temp.setItem(cartItem.getItem());
                temp.setQuantity(cartItem.getQuantity());
                temp = orderService.saveOrderItem(temp);
                order.addOrderItem(temp); //save every OrderItem
            }

            //set user from which account order was placed
            order.setUser(userEntity);

            //save Order
            orderService.saveOrder(order);

            //send email with order info
            emailService.sendOrderConfirmationEmail(userEntity.getEmail(), order, cartService.getCartTotalPrice(userEntity));

            //remove CartItems from Cart & update warehouse quantity
            cartService.deleteAllItemsFromCartAndUpdateWarehouse(userEntity);

            return "redirect:/?successCheckout";
        }
        return "redirect:/";
    }


    @RequestMapping("/removeOne/{itemPublicId}")
    public String removeOneElement(@AuthenticationPrincipal User user, @PathVariable("itemPublicId") UUID itemPublicId) {
        //if item exists
        if(itemService.findItemByPublicId(itemPublicId).isPresent()) {
            if (userEntityService.isUserExistsByUserEntityEmail(user.getUsername())) {
                UserEntity userEntity = userEntityService.findUserEntityByEmail(user.getUsername()).get();
                Item item = itemService.findItemByPublicId(itemPublicId).get();

                //remove item from cart
                cartService.removeItemFromCart(userEntity, itemPublicId, 1);

                //refresh cart before showing view
                Cart cart = cartService.createNewOrFindExistingCart(userEntity);
                cartService.refreshCart(cart);
            }
        }
        //return redirect to same page - cart
        return "redirect:/cart";
    }

    @RequestMapping("/addOne/{itemPublicId}")
    public String addOneElement(@AuthenticationPrincipal User user, @PathVariable("itemPublicId") UUID itemPublicId) {
        //if item exists
        if(itemService.findItemByPublicId(itemPublicId).isPresent()) {
            if(userEntityService.isUserExistsByUserEntityEmail(user.getUsername())) {
                UserEntity userEntity = userEntityService.findUserEntityByEmail(user.getUsername()).get();
                Item item = itemService.findItemByPublicId(itemPublicId).get();

                //add item to cart
                cartService.addItemToCart(userEntity, itemPublicId, 1);

                //refresh cart before showing view
                Cart cart = cartService.createNewOrFindExistingCart(userEntity);
                cartService.refreshCart(cart);
            }
        }
        //return redirect to same page - cart
        return "redirect:/cart";
    }
}
