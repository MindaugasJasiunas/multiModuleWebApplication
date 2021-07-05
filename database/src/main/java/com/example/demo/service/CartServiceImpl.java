package com.example.demo.service;

import com.example.demo.dao.CartItemRepository;
import com.example.demo.dao.CartRepository;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Item;
import com.example.demo.entity.Order;
import com.example.demo.entity.authentication.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.Iterator;
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

        if(userCart.getCartItems()!=null){
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
        }
        //if new item - create and add new in cart
        CartItem cartItem= new CartItem();
        cartItem.setItem(item);
        if(quantityInWarehouse < quantity){
            cartItem.setQuantity(quantityInWarehouse);
        }else{
            cartItem.setQuantity(quantity);
        }
        cartItem= cartItemRepo.save(cartItem);
        userCart.addCartItem(cartItem);
        cartRepo.save(userCart);
    }

    @Override
    public void refreshCart(Cart cart){
        if(cartRepo.findById(cart.getId()).isPresent()){
            if(cart.getCartItems()!=null){
                Iterator<CartItem> i=cart.getCartItems().iterator();
                while(i.hasNext()){
                    CartItem cartItem= i.next();

                    //if quantity in cart(only way - when using buttons in cart) or quantity in warehouse 0 - remove
                    if(cartItem.getQuantity()<1 || itemService.getItemQuantityInWarehouse(cartItem.getItem())<1){
                        cart.getCartItems().remove(cartItem);
                        cartItemRepo.delete(cartItem);
                        return;
                    }
                    //if quantity in cart more than quantity in warehouse - update cart item
                    if(cartItem.getQuantity() > itemService.getItemQuantityInWarehouse(cartItem.getItem())){
                        cartItem.setQuantity(itemService.getItemQuantityInWarehouse(cartItem.getItem()));
                        cartItemRepo.save(cartItem);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public MonetaryAmount getCartTotalPrice(UserEntity userEntity){
        Cart cart= createNewOrFindExistingCart(userEntity);

        CurrencyUnit euro= Monetary.getCurrency("EUR");
        MonetaryAmount total= Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber(0).create();
        for(CartItem cartItem : cart.getCartItems()){
            total= total.add(cartItem.getItem().getPrice().multiply(cartItem.getQuantity()));
        }
        return total;
    }

    @Override
    public int getCartTotalAmountOfItems(UserEntity userEntity){
        Cart cart= createNewOrFindExistingCart(userEntity);
        int total=0;
        for(CartItem cartItem : cart.getCartItems()){
            total+= cartItem.getQuantity();
        }
        return total;
    }


    @Override
    public void deleteAllItemsFromCartAndUpdateWarehouse(UserEntity userEntity){
        Cart cart= createNewOrFindExistingCart(userEntity);
        //delete all items from cart AND change warehouse quantity
        Iterator<CartItem> i= cart.getCartItems().iterator();
        while(i.hasNext()){
            CartItem cartItem=i.next();
            itemService.deleteFromWarehouse(cartItem);
            cartItemRepo.delete(cartItem);
        }
    }


    @Override
    public void removeItemFromCart(UserEntity userEntity, UUID itemPublicId, int quantity){
        //if quantity wanted is 0 or less
        if(quantity<1){
            return;
        }
        //if item exists
        if(itemService.isItemExistsByPublicId(itemPublicId)){
            Item itemFromDB= itemService.findItemByPublicId(itemPublicId).get();
            int quantityInWarehouse= itemService.getItemQuantityInWarehouse(itemFromDB);
            //if quantity in warehouse 0 - return
            if(quantityInWarehouse<1){
                return;
            }
            //quantity in warehouse not 0
            Cart cart= createNewOrFindExistingCart(userEntity);
            //find wanted item
            for(CartItem cartItem: cart.getCartItems()){
                //found CartItem
                if(cartItem.getItem().getPublicId().equals(itemPublicId)){
                    //if new quantity 0 or less - delete cart item
                    if(cartItem.getQuantity()-quantity <1){
                        //delete cartItem
                        cartItemRepo.delete(cartItem);
                    //if new quantity more than 0 - change quantity
                    }else if(cartItem.getQuantity()-quantity >0){
                        cartItem.setQuantity(cartItem.getQuantity()-quantity);
                        //update cartItem
                        cartItemRepo.save(cartItem);
                    }
                }
            }
            //update cart
            refreshCart(cart);
        }
    }

    @Override
    public BindingResult checkBankingInfo(Order order, BindingResult br) {
        // test banking card info

        //check card expiration (can be changed to regex if conditional regex supported)
        //check if not null
        if(order.getExpiration()!=null){
            //check if XX/XX 5 symbols
            if(order.getExpiration().length()==5){
                //check if numbers passed
                try{
                    int expirationMonth= Integer.parseInt(order.getExpiration().substring(0,2));
                    int expirationYear= Integer.parseInt(order.getExpiration().substring(3,5));
                    int month=LocalDate.now().getMonthValue();
                    int year=Integer.parseInt(String.valueOf(LocalDate.now().getYear()).substring(2,4));
                    //check if month is valid
                    if(expirationMonth>=1 && expirationMonth<13){
                        //check if expiration year not in the past
                        if(expirationYear>=year){
                            if((expirationYear==year && expirationMonth>month) || expirationYear>year){
                                // everything is valid
                                return br;
                            }
                        }
                    }
                }catch (NumberFormatException | NullPointerException ignored){
                }
            }
        }
        br.rejectValue("expiration", "error.order", "Check expiration field.");
        return br;
    }
}
