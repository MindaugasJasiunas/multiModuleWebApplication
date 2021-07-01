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
    public String processAddToCart(@AuthenticationPrincipal UserEntity user, @PathVariable("itemPublicId") UUID itemPublicId, @RequestParam(value = "inputQuantity", defaultValue = "0") String quantity) {
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


    @GetMapping("/checkout")
    public String showCheckoutPage(@AuthenticationPrincipal UserEntity user, Model model){
        if(userEntityService.isUserExistsByUserEntityEmail(user.getUsername())) {
            //if there is no items in cart and user going to checkout page - redirect to main page
            if (cartService.getCartTotalAmountOfItems(user) == 0) {
                return "redirect:/";
            }
            Cart userCart = cartService.createNewOrFindExistingCart(user);
            //update cart items before showing
            cartService.refreshCart(userCart);

            model.addAttribute("cartItems", userCart.getCartItems());
            model.addAttribute("cartTotal", cartService.getCartTotalPrice(user));
            model.addAttribute("user", user);
            model.addAttribute("totalNumberOfItems", cartService.getCartTotalAmountOfItems(user));
            model.addAttribute("countries", UtilClass.getCountries());
            model.addAttribute("states", UtilClass.getStatesByCountry("Lithuania"));
            model.addAttribute("contextPath", webpageContextPath);

            Order order=new Order();
            order.setFirstName(user.getFirstName());
            order.setLastName(user.getLastName());
            model.addAttribute("order", order);

            return "checkout";
        }else{
            return "redirect:/";
        }
    }

    @PostMapping("/checkout")
    public String processCheckout(@Valid @ModelAttribute("order") Order order, BindingResult br, @AuthenticationPrincipal UserEntity userEntity, Model model, HttpServletRequest request){
        //if has errors -return to the same page with errors
        if(br.hasErrors()){
            System.out.println(br.getAllErrors());

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
        //if no errors:
        System.out.println(order);

        Cart cart= cartService.createNewOrFindExistingCart(userEntity);
        //refresh cart one last time
        cartService.refreshCart(cart);

        //copy CartItems to OrderItems
        for(CartItem cartItem: cart.getCartItems()){
            OrderItem temp=new OrderItem();
            temp.setItem(cartItem.getItem());
            temp.setQuantity(cartItem.getQuantity());
            temp=orderService.saveOrderItem(temp);
            order.addOrderItem(temp); //save every OrderItem
        }

        // *** send card info to bank ***

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


    @RequestMapping("/removeOne/{itemPublicId}")
    public String removeOneElement(@AuthenticationPrincipal UserEntity user, @PathVariable("itemPublicId") UUID itemPublicId) {
        //if item exists
        if(itemService.findItemByPublicId(itemPublicId).isPresent()) {
            Item item = itemService.findItemByPublicId(itemPublicId).get();

            //add item to cart
            cartService.removeItemFromCart(user, itemPublicId, 1);

            //refresh cart before showing view
            Cart cart= cartService.createNewOrFindExistingCart(user);
            cartService.refreshCart(cart);
        }
        return "redirect:/cart";
    }

    @RequestMapping("/addOne/{itemPublicId}")
    public String addOneElement(@AuthenticationPrincipal UserEntity user, @PathVariable("itemPublicId") UUID itemPublicId) {
        //if item exists
        if(itemService.findItemByPublicId(itemPublicId).isPresent()) {
            Item item = itemService.findItemByPublicId(itemPublicId).get();

            //add item to cart
            cartService.addItemToCart(user, itemPublicId, 1);

            //refresh cart before showing view
            Cart cart= cartService.createNewOrFindExistingCart(user);
            cartService.refreshCart(cart);
        }

        //return redirect to same page - cart
        return "redirect:/cart";
    }
}
