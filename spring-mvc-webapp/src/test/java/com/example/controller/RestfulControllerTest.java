package com.example.controller;

import com.example.ControllerTestConfig;
import com.example.configuration.MyAppConfiguration;
import com.example.configuration.MySpringMvcDispatcherServletInitializer;
import com.example.demo.dao.ItemRepository;
import com.example.demo.dao.StoreItemRepository;
import com.example.demo.dao.StoreRepository;
import com.example.demo.entity.Item;
import com.example.demo.service.ItemService;
import com.example.demo.service.ItemServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.money.Monetary;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration(value = "web") //default :"src/main/webapp"
@ContextConfiguration(classes = {MyAppConfiguration.class, ControllerTestConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestfulControllerTest {
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
    void list() throws Exception {
        List<Item> itemsToPopulatePage= new ArrayList<>();
        for(int i=0; i<10; i++){
            Item temporaryItem=new Item();
            temporaryItem.setId(i);
            temporaryItem.setTitle("itemTitle"+i);
            temporaryItem.setDescription("desc"+i);
            temporaryItem.setPrice(Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(15.15).create());
            itemsToPopulatePage.add(temporaryItem);
        }
        Page<Item> page= new PageImpl<>(itemsToPopulatePage);

        Mockito.when(itemService.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/v1/items"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(10)))  // com.jayway.jsonpath:json-path dependency needed for jsonPath()  // com.hamcrest:hamcrest-library dependency needed for hasSize() and hasProperty()
                    .andExpect(jsonPath("$[0].id", is(0)))
                    .andExpect(jsonPath("$[0].title", is("itemTitle0")))
                    .andExpect(jsonPath("$[0].description", is("desc0")))
                    .andExpect(jsonPath("$[0].price.currency.currencyCode", is("EUR")))
                    .andExpect(jsonPath("$[0].price.number", is(15.15)));


        Mockito.verify(itemService, Mockito.times(1)).findAll(ArgumentMatchers.any(Pageable.class));

    }


    @DisplayName("getStatesByCountry() - Lithuania")
    @Test
    void getStatesByCountry_Lithuania() throws Exception {
        mockMvc.perform(get("/api/v1/states/Lithuania"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Vilnius")))
                .andExpect(jsonPath("$[1]", is("Kaunas")))
                .andExpect(jsonPath("$[2]", is("Klaipeda")));
    }

    @DisplayName("getStatesByCountry() - Latvia")
    @Test
    void getStatesByCountry_Latvia() throws Exception {
        mockMvc.perform(get("/api/v1/states/Latvia"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Riga")))
                .andExpect(jsonPath("$[1]", is("Liepaja")))
                .andExpect(jsonPath("$[2]", is("Jurmala")));
    }

    @DisplayName("getStatesByCountry() - Estonia")
    @Test
    void getStatesByCountry_Estonia() throws Exception {
        mockMvc.perform(get("/api/v1/states/Estonia"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Tallinn")))
                .andExpect(jsonPath("$[1]", is("Tartu")))
                .andExpect(jsonPath("$[2]", is("Parnu")));
    }

    @DisplayName("getStatesByCountry() - UK")
    @Test
    void getStatesByCountry_UK() throws Exception {
        mockMvc.perform(get("/api/v1/states/UK"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("London")))
                .andExpect(jsonPath("$[1]", is("Birmingham")))
                .andExpect(jsonPath("$[2]", is("Manchester")));
    }
}