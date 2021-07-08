package com.example.controller;

import com.example.ControllerTestConfig;
import com.example.configuration.MyAppConfiguration;
import com.example.demo.entity.*;
import com.example.demo.entity.Order;
import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.*;
import com.example.demo.service.authentication.EmailService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;
import org.unbescape.uri.UriEscape;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.naming.Binding;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration(value = "web") //default :"src/main/webapp"
@ContextConfiguration(classes = {MyAppConfiguration.class, ControllerTestConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CartControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    //mock beans from test configuration
    @Autowired
    UserEntityService userEntityService;
    @Autowired
    CartService cartService;
    @Autowired
    ItemService itemService;
    @Autowired
    OrderService orderService;
    @Autowired
    EmailService emailService;

    @BeforeEach
    void setUp() {
        //reset the mocked classes to prevent leakage
        Mockito.reset(userEntityService, cartService, itemService, orderService, emailService);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @DisplayName("showShoppingCartPage() with mock auth user + CUSTOMER role")
    @Test
    @WithMockUser(roles="CUSTOMER")
    void showShoppingCartPage() throws Exception {
        Mockito.when(userEntityService.isUserExistsByUserEntityEmail(nullable(String.class))).thenReturn(true);
        Mockito.when(userEntityService.findUserEntityByEmail(nullable(String.class))).thenReturn(Optional.of(new UserEntity()));
        Cart cart=new Cart();
        CartItem cartItem=new CartItem();
        Item item=new Item();
        item.setPublicId(UUID.randomUUID());
        item.setPrice(Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(1).create());
        cartItem.setItem(item);
        cartItem.setQuantity(1);
        cart.addCartItem(cartItem);
        Mockito.when(cartService.createNewOrFindExistingCart(any(UserEntity.class))).thenReturn(cart);
        Mockito.doNothing().when(cartService).refreshCart(any(Cart.class));
        Mockito.when(cartService.getCartTotalPrice(any(UserEntity.class))).thenReturn(Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(1).create());

        mockMvc
                .perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("cartTotal"))
                .andExpect(view().name("cart"));

        Mockito.verify(userEntityService, Mockito.times(1)).isUserExistsByUserEntityEmail(nullable(String.class));
        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByEmail(nullable(String.class));
        Mockito.verify(cartService, Mockito.times(1)).createNewOrFindExistingCart(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.times(1)).refreshCart(any(Cart.class));
        Mockito.verify(cartService, Mockito.times(1)).getCartTotalPrice(any(UserEntity.class));
    }

    @DisplayName("showShoppingCartPage() with mock auth user + CUSTOMER role - user cannot be found in DB (server error)")
    @Test
    @WithMockUser(roles="CUSTOMER")
    void showShoppingCartPage_userNotFoundInDB() throws Exception {
        Mockito.when(userEntityService.isUserExistsByUserEntityEmail(nullable(String.class))).thenReturn(false);

        mockMvc
                .perform(get("/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Mockito.verify(userEntityService, Mockito.times(1)).isUserExistsByUserEntityEmail(nullable(String.class));
    }

    @DisplayName("showShoppingCartPage() with mock auth user + NOT CUSTOMER role")
    @Test
    @WithMockUser(roles={"ADMIN", "EMPLOYEE"})
    void showShoppingCartPage_wrongRole() throws Exception {
        mockMvc
                .perform(get("/cart"))
                .andExpect(status().isForbidden());
    }

    @DisplayName("showCheckoutPage() without authentication")
    @Test
    void showCheckoutPage_noAuth() throws Exception {
        mockMvc
                .perform(get("/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("showCheckoutPage() with auth user + CUSTOMER role")
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void showCheckoutPage() throws Exception {
        Mockito.when(userEntityService.isUserExistsByUserEntityEmail(nullable(String.class))).thenReturn(true);
        UserEntity userEntity=new UserEntity();
        Mockito.when(userEntityService.findUserEntityByEmail(nullable(String.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(cartService.getCartTotalAmountOfItems(any(UserEntity.class))).thenReturn(5);
        Cart cart=new Cart();
        CartItem cartItem=new CartItem();
        Item item=new Item();
        item.setPublicId(UUID.randomUUID());
        item.setPrice(Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(1.0).create());
        cartItem.setItem(item);
        cartItem.setQuantity(1);
        cart.addCartItem(cartItem);
        Mockito.when(cartService.createNewOrFindExistingCart(any(UserEntity.class))).thenReturn(cart);
        Mockito.doNothing().when(cartService).refreshCart(any(Cart.class));
        Mockito.when(cartService.getCartTotalPrice(any(UserEntity.class))).thenReturn(Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(1.0).create());

        mockMvc
                .perform(get("/checkout"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("cartTotal"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("totalNumberOfItems"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(model().attributeExists("states"))
                .andExpect(model().attributeExists("contextPath"))
                .andExpect(model().attributeExists("order"))
                .andExpect(view().name("checkout"));

        Mockito.verify(userEntityService, Mockito.times(2)).isUserExistsByUserEntityEmail(nullable(String.class));
        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByEmail(nullable(String.class));
        Mockito.verify(cartService, Mockito.times(2)).getCartTotalAmountOfItems(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.times(1)).createNewOrFindExistingCart(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.times(1)).refreshCart(any(Cart.class));
        Mockito.verify(cartService, Mockito.times(1)).getCartTotalPrice(any(UserEntity.class));
    }

    @DisplayName("showCheckoutPage() with auth user + CUSTOMER role - empty cart")
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void showCheckoutPage_emptyCart() throws Exception {
        Mockito.when(userEntityService.isUserExistsByUserEntityEmail(nullable(String.class))).thenReturn(true);
        UserEntity userEntity=new UserEntity();
        Mockito.when(userEntityService.findUserEntityByEmail(nullable(String.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(cartService.getCartTotalAmountOfItems(any(UserEntity.class))).thenReturn(0);

        mockMvc
                .perform(get("/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/")); //redirects because cart is empty

        Mockito.verify(userEntityService, Mockito.times(2)).isUserExistsByUserEntityEmail(nullable(String.class));
        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByEmail(nullable(String.class));
        Mockito.verify(cartService, Mockito.times(1)).getCartTotalAmountOfItems(any(UserEntity.class));
    }

    @DisplayName("showCheckoutPage() with auth user + NOT CUSTOMER role")
    @Test
    @WithMockUser(roles={"ADMIN", "EMPLOYEE"})
    void showCheckoutPage_WrongRole() throws Exception {
        mockMvc
                .perform(get("/checkout"))
                .andExpect(status().isForbidden());
    }

    @DisplayName("processAddToCart() with auth user + CUSTOMER role")
    @Test
    @WithMockUser(roles= {"CUSTOMER"})
    void processAddToCart() throws Exception {
        UUID randomUUID= UUID.randomUUID();
        Item item= new Item();
        item.setPublicId(randomUUID);
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));
        UserEntity userEntity= new UserEntity();
        Mockito.when(userEntityService.isUserExistsByUserEntityEmail(nullable(String.class))).thenReturn(true);
        Mockito.when(userEntityService.findUserEntityByEmail(nullable(String.class))).thenReturn(Optional.of(userEntity));
        final ArgumentCaptor<Integer> intArgumentCaptor=ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(cartService).addItemToCart(any(UserEntity.class), any(UUID.class), intArgumentCaptor.capture());
        int quantity=3;

        mockMvc
                .perform(post("/addToCart/{itemPublicId}", randomUUID).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("inputQuantity", String.valueOf(quantity)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/item/"+randomUUID+"?added"));

        assertEquals(quantity, intArgumentCaptor.getValue());

        Mockito.verify(itemService, Mockito.times(2)).findItemByPublicId(any(UUID.class));
        Mockito.verify(userEntityService, Mockito.times(1)).isUserExistsByUserEntityEmail(nullable(String.class));
        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByEmail(nullable(String.class));
        Mockito.verify(cartService, Mockito.times(1)).addItemToCart(any(UserEntity.class), any(UUID.class), anyInt());
    }

    @DisplayName("removeOneElement() with auth user + CUSTOMER role")
    @Test
    @WithMockUser(roles= {"CUSTOMER"})
    void removeOneElement() throws Exception {
        Mockito.when(userEntityService.isUserExistsByUserEntityEmail(nullable(String.class))).thenReturn(true);
        UserEntity userEntity=new UserEntity();
        Mockito.when(userEntityService.findUserEntityByEmail(nullable(String.class))).thenReturn(Optional.of(userEntity));

        UUID randomUUID= UUID.randomUUID();
        Item item= new Item();
        item.setPublicId(randomUUID);
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));

        Mockito.doNothing().when(cartService).removeItemFromCart(any(UserEntity.class), any(UUID.class), anyInt());
        Cart cart=new Cart();
        Mockito.when(cartService.createNewOrFindExistingCart(any(UserEntity.class))).thenReturn(cart);

        Mockito.doNothing().when(cartService).refreshCart(any(Cart.class));


        mockMvc
                .perform(post("/removeOne/{itemPublicId}", randomUUID).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByEmail(nullable(String.class));
        Mockito.verify(userEntityService, Mockito.times(1)).isUserExistsByUserEntityEmail(nullable(String.class));
        Mockito.verify(itemService, Mockito.times(2)).findItemByPublicId(any(UUID.class));
        Mockito.verify(cartService, Mockito.times(1)).removeItemFromCart(any(UserEntity.class), any(UUID.class), anyInt());
        Mockito.verify(cartService, Mockito.times(1)).createNewOrFindExistingCart(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.times(1)).refreshCart(any(Cart.class));
    }

    @DisplayName("addOneElement() with auth user + CUSTOMER role")
    @Test
    @WithMockUser(roles= {"CUSTOMER"})
    void addOneElement() throws Exception {
        Mockito.when(userEntityService.isUserExistsByUserEntityEmail(nullable(String.class))).thenReturn(true);
        UserEntity userEntity=new UserEntity();
        Mockito.when(userEntityService.findUserEntityByEmail(nullable(String.class))).thenReturn(Optional.of(userEntity));
        UUID randomUUID= UUID.randomUUID();
        Item item= new Item();
        item.setPublicId(randomUUID);
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));
        Mockito.doNothing().when(cartService).addItemToCart(any(UserEntity.class), any(UUID.class), anyInt());
        Cart cart=new Cart();
        Mockito.when(cartService.createNewOrFindExistingCart(any(UserEntity.class))).thenReturn(cart);
        Mockito.doNothing().when(cartService).refreshCart(any(Cart.class));

        mockMvc
                .perform(post("/addOne/{itemPublicId}", randomUUID).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByEmail(nullable(String.class));
        Mockito.verify(userEntityService, Mockito.times(1)).isUserExistsByUserEntityEmail(nullable(String.class));
        Mockito.verify(itemService, Mockito.times(2)).findItemByPublicId(any(UUID.class));
        Mockito.verify(cartService, Mockito.times(1)).addItemToCart(any(UserEntity.class), any(UUID.class), anyInt());
        Mockito.verify(cartService, Mockito.times(1)).createNewOrFindExistingCart(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.times(1)).refreshCart(any(Cart.class));
    }

    //Spring mockMvc doesn't consider validation @Valid - TEST NOT WORKING (Only manual br.rejectValue("fieldName", "error.fieldName", "error message") works)
    @Disabled
    @DisplayName("processCheckout() with auth user + CUSTOMER role - no errors in order")
    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void processCheckout() throws Exception {
        Mockito.when(userEntityService.isUserExistsByUserEntityEmail(nullable(String.class))).thenReturn(true);
        UserEntity userEntity = new UserEntity();
        Mockito.when(userEntityService.findUserEntityByEmail(nullable(String.class))).thenReturn(Optional.of(userEntity));
        BindingResult br = mock(BindingResult.class);

        Mockito.when(cartService.checkBankingInfo(any(Order.class), any(BindingResult.class))).thenReturn(br);

        Mockito.when(br.hasErrors()).thenReturn(false);
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(1);
        Item item = new Item();
        cartItem.setItem(item);
        cart.addCartItem(cartItem);
        Mockito.when(cartService.createNewOrFindExistingCart(any(UserEntity.class))).thenReturn(cart);
        Mockito.doNothing().when(cartService).refreshCart(any(Cart.class));

        Mockito.when(orderService.saveOrderItem(any(OrderItem.class))).thenReturn(new OrderItem());
        Order order = new Order();
        Mockito.when(orderService.saveOrder(any(Order.class))).thenReturn(order);
        Mockito.when(cartService.getCartTotalPrice(any(UserEntity.class))).thenReturn(Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(1).create());
        Mockito.doNothing().when(emailService).sendOrderConfirmationEmail(nullable(String.class), any(Order.class), any(MonetaryAmount.class));
        Mockito.doNothing().when(cartService).deleteAllItemsFromCartAndUpdateWarehouse(any(UserEntity.class));

        mockMvc
                .perform(post("/checkout").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "")  // test should fail but doesn't
                        .param("lastName", "Testy")
                        .param("address", "1234 Street")
                        .param("country", "Lithuania")
                        .param("state", "Vilnius")
                        .param("zipCode", "12345")
                        .param("expiration", "09/99")
                        .param("ccv", "123")
                        .param("creditCardNumber", "5167999988887777")
                        .param("cardName", "Test Testy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeHasNoErrors("order"))
                .andExpect(redirectedUrl("/?successCheckout"));

        Mockito.verify(userEntityService, Mockito.times(1)).isUserExistsByUserEntityEmail(nullable(String.class));
        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByEmail(nullable(String.class));
        Mockito.verify(cartService, Mockito.times(1)).checkBankingInfo(any(Order.class), any(BindingResult.class));
        Mockito.verify(cartService, Mockito.times(1)).createNewOrFindExistingCart(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.times(1)).refreshCart(any(Cart.class));
        Mockito.verify(orderService, Mockito.times(1)).saveOrderItem(any(OrderItem.class));
        Mockito.verify(orderService, Mockito.times(1)).saveOrder(any(Order.class));
        Mockito.verify(cartService, Mockito.times(1)).getCartTotalPrice(any(UserEntity.class));
        Mockito.verify(emailService, Mockito.times(1)).sendOrderConfirmationEmail(nullable(String.class), any(Order.class), any(MonetaryAmount.class));
        Mockito.verify(cartService, Mockito.times(1)).deleteAllItemsFromCartAndUpdateWarehouse(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.never()).getCartTotalAmountOfItems(userEntity);
        Mockito.verify(br, Mockito.times(1)).hasErrors();
    }


    //Spring mockMvc doesn't consider validation @Valid - TEST NOT WORKING (Only manual br.rejectValue("fieldName", "error.fieldName", "error message") works)
    @Disabled
    @DisplayName("processCheckout() with auth user + CUSTOMER role - order has errors")
    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void processCheckout_hasErrors() throws Exception {
        Mockito.when(userEntityService.isUserExistsByUserEntityEmail(nullable(String.class))).thenReturn(true);
        UserEntity userEntity = new UserEntity();
        Mockito.when(userEntityService.findUserEntityByEmail(nullable(String.class))).thenReturn(Optional.of(userEntity));
        BindingResult br = mock(BindingResult.class);

        Mockito.when(cartService.checkBankingInfo(any(Order.class), any(BindingResult.class))).thenReturn(br);

        Mockito.when(br.hasErrors()).thenReturn(true);
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(1);
        Item item = new Item();
        item.setPrice(Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(1).create());
        cartItem.setItem(item);
        cart.addCartItem(cartItem);
        Mockito.when(cartService.createNewOrFindExistingCart(any(UserEntity.class))).thenReturn(cart);
        Mockito.doNothing().when(cartService).refreshCart(any(Cart.class));
        Mockito.when(cartService.getCartTotalPrice(any(UserEntity.class))).thenReturn(Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(1).create());
        Mockito.when(cartService.getCartTotalAmountOfItems(any(UserEntity.class))).thenReturn(5);

        mockMvc
                .perform(post("/checkout").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "") //should have field error
                        .param("lastName", "Testy")
                        .param("address", "1234 Street")
                        .param("country", "Lithuania")
                        .param("state", "Vilnius")
                        .param("zipCode", "12345")
                        .param("expiration", "09/99")
                        .param("ccv", "123")
                        .param("creditCardNumber", "5167999988887777")
                        .param("cardName", "Test Testy"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeHasErrors("order"))
                .andExpect(model().attributeHasFieldErrors("order", "firstName"))
                .andExpect(view().name("checkout"));

        Mockito.verify(userEntityService, Mockito.times(1)).isUserExistsByUserEntityEmail(nullable(String.class));
        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByEmail(nullable(String.class));
        Mockito.verify(cartService, Mockito.times(1)).checkBankingInfo(any(Order.class), any(BindingResult.class));
        Mockito.verify(br, Mockito.times(1)).hasErrors();
        Mockito.verify(cartService, Mockito.times(1)).createNewOrFindExistingCart(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.times(1)).refreshCart(any(Cart.class));
        Mockito.verify(cartService, Mockito.times(1)).getCartTotalPrice(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.times(1)).getCartTotalAmountOfItems(any(UserEntity.class));
    }


}