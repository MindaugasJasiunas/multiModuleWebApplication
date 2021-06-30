package com.example.demo.service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.authentication.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.money.MonetaryAmount;
import java.util.UUID;

public interface CartService {
    Cart createNewOrFindExistingCart(UserEntity userEntity);
    void addItemToCart(UserEntity userEntity, UUID itemPublicId, int quantity);
    void refreshCart(Cart cart);
    MonetaryAmount getCartTotalPrice(UserEntity userEntity);
    int getCartTotalAmountOfItems(UserEntity userEntity);
    void deleteAllItemsFromCartAndUpdateWarehouse(UserEntity userEntity);
    void removeItemFromCart(UserEntity userEntity, UUID itemPublicId, int quantity);
}
