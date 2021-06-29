package com.example.demo.service;

import com.example.demo.dao.CartItemRepository;
import com.example.demo.dao.CartRepository;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Item;
import com.example.demo.entity.authentication.UserEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ItemService itemService;

    public CartServiceImpl(CartRepository cartRepo, CartItemRepository cartItemRepo, ItemService itemService) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.itemService = itemService;
    }

    private boolean isCartByUserEntityExists(UserEntity userEntity){
        if(cartRepo.findCartByUser(userEntity).isPresent()){
            return true;
        }
        return false;
    }

    @Override
    public Cart createNewOrFindExistingCart(UserEntity userEntity){
        if(isCartByUserEntityExists(userEntity)){
            return cartRepo.findCartByUser(userEntity).get();
        }else{
            Cart cart=new Cart();
            cart.setUser(userEntity);
            return cartRepo.save(cart);
        }
    }


    @Override
    public void addItemToCart(UserEntity userEntity, UUID itemPublicId, int quantity){
        if(quantity<1 || !itemService.isItemExistsByPublicId(itemPublicId)){
            return;
        }
        Item item= itemService.findItemByPublicId(itemPublicId).get();
        if(itemService.getItemQuantityInWarehouse(item)<1){
            return;
        }
        Cart userCart= createNewOrFindExistingCart(userEntity);
        int quantityInWarehouse= itemService.getItemQuantityInWarehouse(item);

        //if item already in cart - update existing
        for(CartItem cartItem : userCart.getCartItems()){
            if(cartItem.getItem().getPublicId().equals(item.getPublicId())){
                if(quantityInWarehouse <= (cartItem.getQuantity() + quantity)){
                    //if wanted quantity is more than is in Warehouse - set maximum quantity available
                    cartItem.setQuantity(itemService.getItemQuantityInWarehouse(item));
                }else{
                    //else if quantity that will be added doesn't exceed stock in Warehouse - update
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);
                }
                cartItemRepo.save(cartItem);
                return;
            }
        }
        //if new item - create and add new in cart
        CartItem cartItem= new CartItem();
        cartItem.setItem(item);
        if(quantityInWarehouse < quantity){
            cartItem.setQuantity(quantityInWarehouse);
        }
        cartItem= cartItemRepo.save(cartItem);
        userCart.addCartItem(cartItem);
        cartRepo.save(userCart);
    }


}
