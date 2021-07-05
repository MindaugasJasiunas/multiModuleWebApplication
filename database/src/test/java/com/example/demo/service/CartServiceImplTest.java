package com.example.demo.service;

import com.example.demo.dao.CartItemRepository;
import com.example.demo.dao.CartRepository;
import com.example.demo.entity.*;
import com.example.demo.entity.authentication.UserEntity;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Or;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    @Mock
    private CartRepository cartRepo;
    @Mock
    private CartItemRepository cartItemRepo;
    @Mock
    private ItemService itemService;
    @InjectMocks
    CartServiceImpl cartService;

    @Test
    void createNewOrFindExistingCart_newCart() {
        //given
        UserEntity userEntity=new UserEntity();
        Mockito.when(cartRepo.findCartByUser(userEntity)).thenReturn(Optional.empty());
        Cart cart=new Cart();

        final ArgumentCaptor<Cart> cartArgumentCaptor= ArgumentCaptor.forClass(Cart.class);
        Mockito.when(cartRepo.save(cartArgumentCaptor.capture())).thenReturn(cart);

        //when
        Cart cartCreated= cartService.createNewOrFindExistingCart(userEntity);

        //then
        assertNotNull(cartCreated);
        assertEquals(cart, cartCreated);
        assertEquals(userEntity, cartArgumentCaptor.getValue().getUser());

        Mockito.verify(cartRepo, Mockito.times(1)).findCartByUser(any(UserEntity.class));
        Mockito.verify(cartRepo, Mockito.times(1)).save(any(Cart.class));
    }

    @Test
    void createNewOrFindExistingCart_existing() {
        //given
        UserEntity userEntity=new UserEntity();
        Cart cart=new Cart();
        cart.setUser(userEntity);
        Mockito.when(cartRepo.findCartByUser(userEntity)).thenReturn(Optional.of(cart));

        //when
        Cart cartFromService= cartService.createNewOrFindExistingCart(userEntity);

        //then
        assertNotNull(cartFromService);
        assertEquals(userEntity, cartFromService.getUser());

        Mockito.verify(cartRepo, Mockito.times(2)).findCartByUser(userEntity);
        Mockito.verify(cartRepo, Mockito.never()).save(any(Cart.class));
    }

    @DisplayName("addItemToCart() new item")
    @Test
    void addItemToCart_newItem() {
        //given
        UserEntity userEntity=new UserEntity();
        Item item=new Item();
        item.setPublicId(UUID.randomUUID());
        Cart cart=new Cart();
        cart.setUser(userEntity);
        CartItem cartItem=new CartItem();
        Mockito.when(itemService.isItemExistsByPublicId(any(UUID.class))).thenReturn(true);
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));
        Mockito.when(itemService.getItemQuantityInWarehouse(item)).thenReturn(10);

        Mockito.when(cartRepo.findCartByUser(userEntity)).thenReturn(Optional.of(cart));
        final ArgumentCaptor<CartItem> cartItemArgumentCaptor=ArgumentCaptor.forClass(CartItem.class);
        Mockito.when(cartItemRepo.save(cartItemArgumentCaptor.capture())).thenReturn(cartItem);
        final ArgumentCaptor<Cart> cartArgumentCaptor=ArgumentCaptor.forClass(Cart.class);
        Mockito.when(cartRepo.save(cartArgumentCaptor.capture())).thenReturn(cart);

        //when
        cartService.addItemToCart(userEntity, item.getPublicId(), 5);

        //then
        assertEquals(item, cartItemArgumentCaptor.getValue().getItem());
        assertEquals(5, cartItemArgumentCaptor.getValue().getQuantity());
        assertEquals(userEntity, cartArgumentCaptor.getValue().getUser());
        assertEquals(1, cartArgumentCaptor.getValue().getCartItems().size());


        Mockito.verify(itemService, Mockito.times(1)).isItemExistsByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(1)).findItemByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(2)).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartRepo, Mockito.times(2)).findCartByUser(any(UserEntity.class));

        Mockito.verify(cartItemRepo, Mockito.times(1)).save(any(CartItem.class));
        Mockito.verify(cartRepo, Mockito.times(1)).save(any(Cart.class));
    }

    @DisplayName("addItemToCart() new item with quantity more than there is in warehouse - add only what is in warehouse")
    @Test
    void addItemToCart_newItem_quantityOverflow() {
        //given
        UserEntity userEntity=new UserEntity();
        Item item=new Item();
        item.setPublicId(UUID.randomUUID());
        Cart cart=new Cart();
        cart.setUser(userEntity);
        CartItem cartItem=new CartItem();
        Mockito.when(itemService.isItemExistsByPublicId(any(UUID.class))).thenReturn(true);
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));
        Mockito.when(itemService.getItemQuantityInWarehouse(item)).thenReturn(10);

        Mockito.when(cartRepo.findCartByUser(userEntity)).thenReturn(Optional.of(cart));
        final ArgumentCaptor<CartItem> cartItemArgumentCaptor=ArgumentCaptor.forClass(CartItem.class);
        Mockito.when(cartItemRepo.save(cartItemArgumentCaptor.capture())).thenReturn(cartItem);
        final ArgumentCaptor<Cart> cartArgumentCaptor=ArgumentCaptor.forClass(Cart.class);
        Mockito.when(cartRepo.save(cartArgumentCaptor.capture())).thenReturn(cart);

        //when
        cartService.addItemToCart(userEntity, item.getPublicId(), 25);

        //then
        assertEquals(item, cartItemArgumentCaptor.getValue().getItem());
        assertEquals(10, cartItemArgumentCaptor.getValue().getQuantity());
        assertEquals(userEntity, cartArgumentCaptor.getValue().getUser());
        assertEquals(1, cartArgumentCaptor.getValue().getCartItems().size());


        Mockito.verify(itemService, Mockito.times(1)).isItemExistsByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(1)).findItemByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(2)).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartRepo, Mockito.times(2)).findCartByUser(any(UserEntity.class));

        Mockito.verify(cartItemRepo, Mockito.times(1)).save(any(CartItem.class));
        Mockito.verify(cartRepo, Mockito.times(1)).save(any(Cart.class));
    }

    @DisplayName("addItemToCart() item quantity 0 - dont add")
    @Test
    void addItemToCart_quantityZero() {
        //when
        cartService.addItemToCart(new UserEntity(), UUID.randomUUID(), 0);

        //then
        Mockito.verify(itemService, Mockito.never()).isItemExistsByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.never()).findItemByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.never()).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartRepo, Mockito.never()).findCartByUser(any(UserEntity.class));

        Mockito.verify(cartItemRepo, Mockito.never()).save(any(CartItem.class));
        Mockito.verify(cartRepo, Mockito.never()).save(any(Cart.class));
    }

    @DisplayName("addItemToCart() item quantity in warehouse 0 - dont add")
    @Test
    void addItemToCart_warehousequantityZero() {
        //given
        Mockito.when(itemService.isItemExistsByPublicId(any(UUID.class))).thenReturn(true);
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(new Item()));
        Mockito.when(itemService.getItemQuantityInWarehouse(any(Item.class))).thenReturn(0);
        //when
        cartService.addItemToCart(new UserEntity(), UUID.randomUUID(), 5);

        //then
        Mockito.verify(itemService, Mockito.times(1)).isItemExistsByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(1)).findItemByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(1)).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartRepo, Mockito.never()).findCartByUser(any(UserEntity.class));

        Mockito.verify(cartItemRepo, Mockito.never()).save(any(CartItem.class));
        Mockito.verify(cartRepo, Mockito.never()).save(any(Cart.class));
    }

    @DisplayName("addItemToCart() existing item")
    @Test
    void addItemToCart_existingItem() {
        //given
        UserEntity userEntity =new UserEntity();
        Item item=new Item();
        item.setPublicId(UUID.randomUUID());
        CartItem cartItem=new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(3);
        Mockito.when(itemService.isItemExistsByPublicId(any(UUID.class))).thenReturn(true);
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));
        Mockito.when(itemService.getItemQuantityInWarehouse(any(Item.class))).thenReturn(5);
        Cart cart=new Cart();
        cart.setUser(userEntity);
        cart.setCartItems(Arrays.asList(cartItem));
        Mockito.when(cartRepo.findCartByUser(any(UserEntity.class))).thenReturn(Optional.of(cart));

        final ArgumentCaptor<CartItem> cartItemArgumentCaptor=ArgumentCaptor.forClass(CartItem.class);
        Mockito.when(cartItemRepo.save(cartItemArgumentCaptor.capture())).thenReturn(cartItem);

        //when
        cartService.addItemToCart(userEntity, item.getPublicId(), 1);

        //then
        assertEquals(item, cartItemArgumentCaptor.getValue().getItem());
        assertEquals(4, cartItemArgumentCaptor.getValue().getQuantity());

        Mockito.verify(itemService, Mockito.times(1)).isItemExistsByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(1)).findItemByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(2)).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartRepo, Mockito.times(2)).findCartByUser(any(UserEntity.class));
        Mockito.verify(cartItemRepo, Mockito.times(1)).save(any(CartItem.class));
    }

    @DisplayName("addItemToCart() existing item with quantity bigger than in warehouse")
    @Test
    void addItemToCart_existingItem_quantityOverflow() {
        //given
        UserEntity userEntity =new UserEntity();
        Item item=new Item();
        item.setPublicId(UUID.randomUUID());
        CartItem cartItem=new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(5);
        Mockito.when(itemService.isItemExistsByPublicId(any(UUID.class))).thenReturn(true);
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));
        Mockito.when(itemService.getItemQuantityInWarehouse(any(Item.class))).thenReturn(5);
        Cart cart=new Cart();
        cart.setUser(userEntity);
        cart.setCartItems(Arrays.asList(cartItem));
        Mockito.when(cartRepo.findCartByUser(any(UserEntity.class))).thenReturn(Optional.of(cart));

        final ArgumentCaptor<CartItem> cartItemArgumentCaptor=ArgumentCaptor.forClass(CartItem.class);
        Mockito.when(cartItemRepo.save(cartItemArgumentCaptor.capture())).thenReturn(cartItem);

        //when
        cartService.addItemToCart(userEntity, item.getPublicId(), 9);

        //then
        assertEquals(item, cartItemArgumentCaptor.getValue().getItem());
        assertEquals(5, cartItemArgumentCaptor.getValue().getQuantity());

        Mockito.verify(itemService, Mockito.times(1)).isItemExistsByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(1)).findItemByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(3)).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartRepo, Mockito.times(2)).findCartByUser(any(UserEntity.class));
        Mockito.verify(cartItemRepo, Mockito.times(1)).save(any(CartItem.class));
    }

    @DisplayName("refreshCart() cart is empty")
    @Test
    void refreshCart_cartIsEmpty() {
        //given
        Cart cart=new Cart();
        Mockito.when(cartRepo.findById(anyLong())).thenReturn(Optional.of(cart));

        //when
        cartService.refreshCart(cart);

        //then
        Mockito.verify(itemService, Mockito.never()).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartItemRepo, Mockito.never()).delete(any(CartItem.class));
        Mockito.verify(cartItemRepo, Mockito.never()).save(any(CartItem.class));
    }


    @DisplayName("refreshCart() item quantity zero - delete item from cart")
    @Test
    void refreshCart_itemQuantityZero() {
        //given
        Item item=new Item();
        Cart cart=new Cart();
        CartItem cartItem=new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(0);
        cart.addCartItem(cartItem);

        Mockito.when(cartRepo.findById(anyLong())).thenReturn(Optional.of(cart));
        Mockito.doNothing().when(cartItemRepo).delete(any(CartItem.class));

        //when
        cartService.refreshCart(cart);

        //then
        assertEquals(0, cart.getCartItems().size());

        Mockito.verify(itemService, Mockito.never()).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartItemRepo, Mockito.times(1)).delete(any(CartItem.class));
        Mockito.verify(cartItemRepo, Mockito.never()).save(any(CartItem.class));
    }

    @DisplayName("refreshCart() item quantity in Warehouse 0 - delete item from cart")
    @Test
    void refreshCart_itemQuantityInWarehouseIsZero() {
        //given
        Item item=new Item();
        Cart cart=new Cart();
        CartItem cartItem=new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(5);
        cart.addCartItem(cartItem);

        Mockito.when(cartRepo.findById(anyLong())).thenReturn(Optional.of(cart));
        Mockito.when(itemService.getItemQuantityInWarehouse(any(Item.class))).thenReturn(0);
        Mockito.doNothing().when(cartItemRepo).delete(any(CartItem.class));

        //when
        cartService.refreshCart(cart);

        //then
        assertEquals(0, cart.getCartItems().size());

        Mockito.verify(itemService, Mockito.times(1)).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartItemRepo, Mockito.times(1)).delete(any(CartItem.class));
        Mockito.verify(cartItemRepo, Mockito.never()).save(any(CartItem.class));
    }

    @DisplayName("refreshCart() item quantity is bigger than stock in Warehouse - update item to quantity from warehouse")
    @Test
    void refreshCart_itemQuantityMoreThanInWarehouse() {
        //given
        int quantityInWarehouse=7;
        int quantityInCart=90;
        Item item=new Item();
        Cart cart=new Cart();
        CartItem cartItem=new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(quantityInCart);
        cart.addCartItem(cartItem);

        Mockito.when(cartRepo.findById(anyLong())).thenReturn(Optional.of(cart));
        Mockito.when(itemService.getItemQuantityInWarehouse(any(Item.class))).thenReturn(quantityInWarehouse);
        final ArgumentCaptor<CartItem> cartItemArgumentCaptor=ArgumentCaptor.forClass(CartItem.class);
        Mockito.when(cartItemRepo.save(cartItemArgumentCaptor.capture())).thenReturn(cartItem);

        //when
        cartService.refreshCart(cart);

        //then
        assertEquals(1, cart.getCartItems().size());
        assertEquals(quantityInWarehouse, cartItemArgumentCaptor.getValue().getQuantity());
        assertEquals(item, cartItemArgumentCaptor.getValue().getItem());

        Mockito.verify(itemService, Mockito.times(3)).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartItemRepo, Mockito.never()).delete(any(CartItem.class));
        Mockito.verify(cartItemRepo, Mockito.times(1)).save(any(CartItem.class));
    }

    @DisplayName("getCartTotalPrice() empty cart - returns EUR 0")
    @Test
    void getCartTotalPrice_emptyCart() {
        //given
        UserEntity userEntity= new UserEntity();
        Cart cart=new Cart();
        cart.setUser(userEntity);
        Mockito.when(cartRepo.findCartByUser(any(UserEntity.class))).thenReturn(Optional.of(cart));

        //when
        MonetaryAmount amount= cartService.getCartTotalPrice(userEntity);

        //then
        assertNotNull(amount);
        assertEquals("EUR", amount.getCurrency().getCurrencyCode());
        assertEquals(0, amount.getNumber().doubleValueExact());
    }

    @DisplayName("getCartTotalPrice()")
    @Test
    void getCartTotalPrice() {
        //given
        double itemPrice=20.45;
        UserEntity userEntity= new UserEntity();
        Item item=new Item();
        MonetaryAmount price= Monetary.getDefaultAmountFactory().setCurrency(Monetary.getCurrency("EUR")).setNumber(itemPrice).create();
        item.setPrice(price);
        Cart cart=new Cart();
        cart.setUser(userEntity);
        CartItem cartItem=new CartItem();
        cartItem.setQuantity(2);
        cartItem.setItem(item);
        cart.addCartItem(cartItem);
        Mockito.when(cartRepo.findCartByUser(any(UserEntity.class))).thenReturn(Optional.of(cart));

        //when
        MonetaryAmount amount= cartService.getCartTotalPrice(userEntity);

        //then
        assertNotNull(amount);
        assertEquals("EUR", amount.getCurrency().getCurrencyCode());
        assertEquals(itemPrice*2, amount.getNumber().doubleValueExact());
    }

    @Test
    void getCartTotalAmountOfItems() {
        //given
        UserEntity userEntity= new UserEntity();
        Item item=new Item();
        Cart cart=new Cart();
        cart.setUser(userEntity);
        CartItem cartItem=new CartItem();
        cartItem.setQuantity(2);
        cartItem.setItem(item);
        cart.addCartItem(cartItem);

        Mockito.when(cartRepo.findCartByUser(userEntity)).thenReturn(Optional.of(cart));

        //when
        int total= cartService.getCartTotalAmountOfItems(userEntity);

        //then
        assertEquals(2, total);
        Mockito.verify(cartRepo, Mockito.times(2)).findCartByUser(any(UserEntity.class));
    }

    @DisplayName("getCartTotalAmountOfItems() empty cart")
    @Test
    void getCartTotalAmountOfItems_noItems() {
        //given
        UserEntity userEntity= new UserEntity();
        Item item=new Item();
        Cart cart=new Cart();
        cart.setUser(userEntity);

        Mockito.when(cartRepo.findCartByUser(userEntity)).thenReturn(Optional.of(cart));

        //when
        int total= cartService.getCartTotalAmountOfItems(userEntity);

        //then
        assertEquals(0, total);
        Mockito.verify(cartRepo, Mockito.times(2)).findCartByUser(any(UserEntity.class));
    }

    @DisplayName("deleteAllItemsFromCartAndUpdateWarehouse() updates only cart (itemService updates warehouse)")
    @Test
    void deleteAllItemsFromCartAndUpdateWarehouse() {
        //given
        UserEntity userEntity= new UserEntity();
        Item item=new Item();
        Cart cart=new Cart();
        cart.setUser(userEntity);
        CartItem cartItem=new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(5);
        cart.addCartItem(cartItem);

        Mockito.when(cartRepo.findCartByUser(any(UserEntity.class))).thenReturn(Optional.of(cart));
        Mockito.doNothing().when(itemService).deleteFromWarehouse(any(CartItem.class));
        Mockito.doNothing().when(cartItemRepo).delete(any(CartItem.class));

        //when
        cartService.deleteAllItemsFromCartAndUpdateWarehouse(userEntity);

        //then
        Mockito.verify(cartRepo, Mockito.times(2)).findCartByUser(userEntity);
        Mockito.verify(itemService, Mockito.times(1)).deleteFromWarehouse(any(CartItem.class));
        Mockito.verify(cartItemRepo, Mockito.times(1)).delete(any(CartItem.class));
    }

    @DisplayName("removeItemFromCart() quantity to remove 0 - return")
    @Test
    void removeItemFromCart_quantityZero() {
        //given
        UserEntity userEntity=new UserEntity();
        Item item=new Item();
        item.setPublicId(UUID.randomUUID());

        //when
        cartService.removeItemFromCart(userEntity, item.getPublicId(), 0);

        //then
        Mockito.verify(itemService, Mockito.never()).isItemExistsByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.never()).findItemByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.never()).getItemQuantityInWarehouse(any(Item.class));
    }

    @DisplayName("removeItemFromCart() 0 left in warehouse - remove item from cart")
    @Test
    void removeItemFromCart_leftInWarehouseZero() {
        //given
        UserEntity userEntity=new UserEntity();
        Item item=new Item();
        item.setPublicId(UUID.randomUUID());
        Cart cart=new Cart();
        cart.setUser(userEntity);
        CartItem cartItem=new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(2);
        cart.addCartItem(cartItem);

        Mockito.when(itemService.isItemExistsByPublicId(any(UUID.class))).thenReturn(true);
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));
        Mockito.when(itemService.getItemQuantityInWarehouse(any(Item.class))).thenReturn(2);
        Mockito.when(cartRepo.findCartByUser(any(UserEntity.class))).thenReturn(Optional.of(cart));

        //when
        cartService.removeItemFromCart(userEntity, item.getPublicId(), 2);

        //then
        Mockito.verify(itemService, Mockito.times(1)).isItemExistsByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(1)).findItemByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(1)).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartRepo, Mockito.times(2)).findCartByUser(any(UserEntity.class));
        Mockito.verify(cartItemRepo, Mockito.times(1)).delete(any(CartItem.class));
    }

    @Test
    void removeItemFromCart() {
        //given
        UserEntity userEntity=new UserEntity();
        Item item=new Item();
        item.setPublicId(UUID.randomUUID());
        Cart cart=new Cart();
        cart.setUser(userEntity);
        CartItem cartItem=new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(5);
        cart.addCartItem(cartItem);

        Mockito.when(itemService.isItemExistsByPublicId(any(UUID.class))).thenReturn(true);
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));
        Mockito.when(itemService.getItemQuantityInWarehouse(any(Item.class))).thenReturn(10);
        Mockito.when(cartRepo.findCartByUser(any(UserEntity.class))).thenReturn(Optional.of(cart));
        final ArgumentCaptor<CartItem> cartItemArgumentCaptor=ArgumentCaptor.forClass(CartItem.class);
        Mockito.when(cartItemRepo.save(cartItemArgumentCaptor.capture())).thenReturn(cartItem);

        //when
        cartService.removeItemFromCart(userEntity, item.getPublicId(), 2);

        //then
        assertEquals(3, cartItemArgumentCaptor.getValue().getQuantity());
        assertEquals(item, cartItemArgumentCaptor.getValue().getItem());
        Mockito.verify(itemService, Mockito.times(1)).isItemExistsByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(1)).findItemByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(1)).getItemQuantityInWarehouse(any(Item.class));
        Mockito.verify(cartRepo, Mockito.times(2)).findCartByUser(any(UserEntity.class));
        Mockito.verify(cartItemRepo, Mockito.never()).delete(any(CartItem.class));
    }

    @DisplayName("checkBankingInfo() next year")
    @Test
    void checkBankingInfo() {
        //given
        String nextYear=String.valueOf(LocalDate.now().getYear()+1).substring(2,4);
        String thisMonth=String.valueOf(LocalDate.now().getMonthValue()).length()==2 ? String.valueOf(LocalDate.now().getMonthValue()) : "0"+String.valueOf(LocalDate.now().getMonthValue());
        Order order= new Order();
        order.setExpiration(thisMonth+"/"+nextYear);
        BindingResult initializeBindingResult= new DataBinder(new Order()).getBindingResult();

        //when
        BindingResult br= cartService.checkBankingInfo(order, initializeBindingResult);

        //then
        assertFalse(br.hasErrors());
    }

    @DisplayName("checkBankingInfo() wrong format")
    @Test
    void checkBankingInfo_wrongFormat() {
        //given
        Order order= new Order();
        order.setExpiration("1//99");
        BindingResult initializeBindingResult= new DataBinder(new Order()).getBindingResult();

        //when
        BindingResult br= cartService.checkBankingInfo(order, initializeBindingResult);

        //then
        assertTrue(br.hasErrors());
    }

    @DisplayName("checkBankingInfo() expired month (last month)")
    @Test
    void checkBankingInfo_ExpiredMonth() {
        String thisYear=String.valueOf(LocalDate.now().getYear()).substring(2,4);
        int thisMonth=LocalDate.now().getMonthValue();
        String lastMonth=String.valueOf(thisMonth-1).length()==2 ? String.valueOf(thisMonth-1) : "0"+String.valueOf(thisMonth-1) ;
        if(lastMonth.equals("00")){
            lastMonth="12";
            thisYear=String.valueOf(Integer.parseInt(thisYear)-1);
        }
        //given
        Order order= new Order();
        order.setExpiration(lastMonth+"/"+thisYear);
        BindingResult initializeBindingResult= new DataBinder(new Order()).getBindingResult();

        //when
        BindingResult br= cartService.checkBankingInfo(order, initializeBindingResult);

        //then
        assertTrue(br.hasErrors());
    }

    @DisplayName("checkBankingInfo() expired year")
    @Test
    void checkBankingInfo_ExpiredYear() {
        String lastYear=String.valueOf(LocalDate.now().getYear()-1).substring(2,4);
        String thisMonth=String.valueOf(LocalDate.now().getMonthValue()).length()==2 ? String.valueOf(LocalDate.now().getMonthValue()) : "0"+String.valueOf(LocalDate.now().getMonthValue());
        //given
        Order order= new Order();
        order.setExpiration(thisMonth+"/"+lastYear);
        BindingResult initializeBindingResult= new DataBinder(new Order()).getBindingResult();

        //when
        BindingResult br= cartService.checkBankingInfo(order, initializeBindingResult);

        //then
        assertTrue(br.hasErrors());
    }

}