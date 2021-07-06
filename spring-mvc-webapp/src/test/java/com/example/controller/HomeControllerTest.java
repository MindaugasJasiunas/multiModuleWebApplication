package com.example.controller;

import com.example.demo.entity.Category;
import com.example.demo.entity.Gender;
import com.example.demo.entity.Size;
import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.ItemService;
import com.example.demo.service.ItemSortingService;
import com.example.demo.service.UserEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class HomeControllerTest {
    private MockMvc mockMvc;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemSortingService itemSortingService;
    @Mock
    private UserEntityService userEntityService;
    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    @Test
    public void showProductListPage() throws Exception {
        when(itemSortingService.getGenders()).thenReturn(Arrays.asList(new Gender()));
        when(itemSortingService.getCategories()).thenReturn(Arrays.asList(new Category()));
        when(itemSortingService.getSizes()).thenReturn(Arrays.asList(new Size()));
        when(itemService.pageCount(anyInt())).thenReturn(1L);


        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("genderList"))
                .andExpect(model().attributeExists("categoryList"))
                .andExpect(model().attributeExists("sizeList"))
                .andExpect(model().attributeExists("pageCount"))
                .andExpect(view().name("product-list-page"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        Mockito.verify(itemSortingService, Mockito.times(1)).getGenders();
        Mockito.verify(itemSortingService, Mockito.times(1)).getCategories();
        Mockito.verify(itemSortingService, Mockito.times(1)).getSizes();
        Mockito.verify(itemService, Mockito.times(1)).pageCount(anyInt());
    }

    @Test
    void showAdminPage() throws Exception {
        this.mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminpanel"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }


    @Test
    void addAttributes() {
        //given
        UserEntity applicationUser = new UserEntity();
        applicationUser.setFirstName("John");
        applicationUser.setLastName("Doe");

        //mocking spring security authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
//        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);

        Mockito.when(authentication.getName()).thenReturn("fakeEmail");
        Mockito.when(userEntityService.findUserEntityByEmail(anyString())).thenReturn(Optional.of(applicationUser));

        UserEntityService userEntityService= mock(UserEntityService.class);
        UserEntity userEntity=new UserEntity();
//        Mockito.when(userEntityService.findUserEntityByEmail(anyString())).thenReturn(Optional.of(userEntity));

        Model model= mock(Model.class);

        Mockito.when(model.addAttribute("userName", applicationUser.getFirstName()+" "+applicationUser.getLastName())).thenReturn(model);

        //when
        homeController.addAttributes(model);

        //then
        Mockito.verify(securityContext, Mockito.times(2)).getAuthentication();
        Mockito.verify(model, Mockito.times(1)).addAttribute("userName", applicationUser.getFirstName()+" "+applicationUser.getLastName());
        Mockito.verify(authentication, Mockito.times(1)).getName();
    }
}