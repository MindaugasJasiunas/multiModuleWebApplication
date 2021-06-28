package com.example.demo.service;

import com.example.demo.dao.CartRepository;
import com.example.demo.entity.Cart;
import com.example.demo.entity.authentication.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepo;
    private final UserEntityService userEntityService;

    public CartServiceImpl(CartRepository cartRepo, UserEntityService userEntityService) {
        this.cartRepo = cartRepo;
        this.userEntityService = userEntityService;
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




}
