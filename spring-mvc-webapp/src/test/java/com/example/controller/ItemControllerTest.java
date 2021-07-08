package com.example.controller;

import com.example.ControllerTestConfig;
import com.example.configuration.MyAppConfiguration;
import com.example.demo.entity.Item;
import com.example.demo.entity.Store;
import com.example.demo.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.money.Monetary;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration(value = "web") //default :"src/main/webapp"
@ContextConfiguration(classes = {MyAppConfiguration.class, ControllerTestConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    //mock beans from test configuration
    @Autowired
    ItemService itemService;

    @BeforeEach
    void setUp() {
        //reset the mocked classes to prevent leakage
        Mockito.reset(itemService);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    void getItem() throws Exception {
        Item item=new Item();
        item.setPrice(Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(1).create());
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));
        Mockito.when(itemService.getMapWithStoresAndQuantitiesForItem(any(Item.class))).thenReturn(new HashMap<>());
        Mockito.when(itemService.getItemsForRelatedProducts(anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(itemService.getItemQuantityInWarehouse(any(Item.class))).thenReturn(1);

        mockMvc
                .perform(get("/item/{itemPublicId}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("relatedItems"))
                .andExpect(model().attributeExists("storeWithQuantityMap"))
                .andExpect(model().attributeExists("leftInWarehouse"))
                .andExpect(model().attributeExists("item"))
                .andExpect(view().name("product-page"));

        Mockito.verify(itemService, Mockito.times(1)).findItemByPublicId(any(UUID.class));
        Mockito.verify(itemService, Mockito.times(1)).getMapWithStoresAndQuantitiesForItem(any(Item.class));
        Mockito.verify(itemService, Mockito.times(1)).getItemsForRelatedProducts(anyInt());
        Mockito.verify(itemService, Mockito.times(1)).getItemQuantityInWarehouse(any(Item.class));
    }

    @DisplayName("getItem() item not found - 404 page")
    @Test
    void getItem_notFound() throws Exception {
        Mockito.when(itemService.findItemByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        mockMvc
                .perform(get("/item/{itemPublicId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

    }


}