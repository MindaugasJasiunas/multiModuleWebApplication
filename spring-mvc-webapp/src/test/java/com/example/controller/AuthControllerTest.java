package com.example.controller;

import com.example.ControllerTestConfig;
import com.example.configuration.MyAppConfiguration;
import com.example.demo.entity.authentication.AccountVerification;
import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.UserEntityService;
import com.example.demo.service.authentication.EmailService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration(value = "web") //default :"src/main/webapp"
@ContextConfiguration(classes = {MyAppConfiguration.class, ControllerTestConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    //mock beans from test configuration
    @Autowired
    EmailService emailService;
    @Autowired
    UserEntityService userEntityService;

    @BeforeEach
    void setUp() {
        //reset the mocked classes to prevent leakage
        Mockito.reset(emailService, userEntityService);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    void showRegisterPage() throws Exception {
        mockMvc
            .perform(get("/register"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("user"))
            .andExpect(view().name("register"));
    }

    @Test
    void login() throws Exception {
        mockMvc
                .perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void showForgotPasswordPage() throws Exception {
        mockMvc
                .perform(get("/forgot"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"));
    }

    @Test
    void newPasswordForm() throws Exception {
        mockMvc
                .perform(get("/verify/{uuid}/new", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("passwordReset"))
                .andExpect(model().attributeExists("uuid"))
                .andExpect(view().name("new-password-form"));
    }

    @Test
    void verify() throws Exception {
        AccountVerification accountVerification=new AccountVerification();
        accountVerification.setUserEntityPublicId(UUID.randomUUID());
        accountVerification.setResetPassword(false);
        Mockito.when(emailService.findAccountVerificationByVerificationCode(any(UUID.class))).thenReturn(Optional.of(accountVerification));
        UserEntity userEntity= new UserEntity();
        userEntity.setEmail("abc");
        Mockito.when(userEntityService.findUserEntityByPublicId(any(UUID.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(userEntityService.makeUserEnabledByEmail(anyString())).thenReturn(true);
        Mockito.doNothing().when(emailService).deleteAccountVerificationByUserEntityPublicId(any(UUID.class));

        mockMvc
                .perform(get("/verify/{uuid}", UUID.randomUUID()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Mockito.verify(emailService, Mockito.times(2)).findAccountVerificationByVerificationCode(any(UUID.class));
        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByPublicId(any(UUID.class));
        Mockito.verify(userEntityService, Mockito.times(1)).makeUserEnabledByEmail(anyString());
        Mockito.verify(emailService, Mockito.times(1)).deleteAccountVerificationByUserEntityPublicId(any(UUID.class));
    }

    @DisplayName("verify() - new password")
    @Test
    void verify_newPassword() throws Exception {
        AccountVerification accountVerification=new AccountVerification();
        accountVerification.setUserEntityPublicId(UUID.randomUUID());
        accountVerification.setResetPassword(true);
        Mockito.when(emailService.findAccountVerificationByVerificationCode(any(UUID.class))).thenReturn(Optional.of(accountVerification));

        UUID uuid= UUID.randomUUID();
        mockMvc
                .perform(get("/verify/{uuid}", uuid))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/verify/"+uuid+"/new"));

        Mockito.verify(emailService, Mockito.times(2)).findAccountVerificationByVerificationCode(any(UUID.class));
        Mockito.verify(userEntityService, Mockito.never()).findUserEntityByPublicId(any(UUID.class));
        Mockito.verify(userEntityService, Mockito.never()).makeUserEnabledByEmail(anyString());
        Mockito.verify(emailService, Mockito.never()).deleteAccountVerificationByUserEntityPublicId(any(UUID.class));
    }

    @DisplayName("processForgotPasswordPage() - no email provided")
    @Test
    void processForgotPasswordPage_noEmail() throws Exception{
        mockMvc
                .perform(post("/forgot").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot?error"));
    }

    @DisplayName("processForgotPasswordPage() - verification token already saved & sent by email")
    @Test
    void processForgotPasswordPage_alreadyVerificationTokenSent() throws Exception{
        String email= "temp@email";
        Mockito.when(emailService.isAlreadyAccountVerificationByUserEmail(anyString())).thenReturn(true);
        Mockito.doNothing().when(emailService).sendVerificationEmail(email, true);
        mockMvc
                .perform(post("/forgot")
                        .with(csrf())
                        .param("email", email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?successRegistration"));

        Mockito.verify(emailService, Mockito.times(2)).isAlreadyAccountVerificationByUserEmail(anyString());
        Mockito.verify(emailService, Mockito.times(1)).sendVerificationEmail(anyString(), anyBoolean());
        Mockito.verify(userEntityService, Mockito.never()).findUserEntityByEmail(anyString());
    }

    @Test
    void processForgotPasswordPage() throws Exception{
        String email= "temp@email";
        Mockito.when(emailService.isAlreadyAccountVerificationByUserEmail(anyString())).thenReturn(false);
        Mockito.when(userEntityService.findUserEntityByEmail(anyString())).thenReturn(Optional.of(new UserEntity()));
        Mockito.when(userEntityService.makeUserDisabledByEmail(anyString())).thenReturn(true);
        Mockito.doNothing().when(emailService).sendVerificationEmail(email, true);
        mockMvc
                .perform(post("/forgot")
                        .with(csrf())
                        .param("email", email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?successRegistration"));

        Mockito.verify(emailService, Mockito.times(1)).isAlreadyAccountVerificationByUserEmail(anyString());
        Mockito.verify(userEntityService, Mockito.times(1)).findUserEntityByEmail(anyString());
        Mockito.verify(userEntityService, Mockito.times(1)).makeUserDisabledByEmail(anyString());
        Mockito.verify(emailService, Mockito.times(1)).sendVerificationEmail(anyString(), anyBoolean());
    }

    @DisplayName("processNewPasswordForm() - passwords mismatch")
    @Test
    void processNewPasswordForm_passwordsMismatch() throws Exception {
        UUID uuid= UUID.randomUUID();

        mockMvc
                .perform(post("/verify/{uuid}/new",uuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", "pass")
                        .param("repeatPassword", "passMismatch"))
                .andExpect(model().attributeExists("passwordReset"))
                .andExpect(model().attributeHasErrors("passwordReset"))
                .andExpect(status().isOk())
                .andExpect(view().name("new-password-form"));
    }

    @Test
    void processNewPasswordForm() throws Exception {
        UUID uuid = UUID.randomUUID();
        String password="pass";
        AccountVerification accountVerification = new AccountVerification();
        accountVerification.setUserEntityPublicId(uuid);
        Mockito.when(emailService.findAccountVerificationByVerificationCode(any(UUID.class))).thenReturn(Optional.of(accountVerification));
        UserEntity userEntity = new UserEntity();
        userEntity.setPublicId(uuid);
        Mockito.when(userEntityService.findUserEntityByPublicId(any(UUID.class))).thenReturn(Optional.of(userEntity));
        final ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userEntityService.saveOrUpdate(userEntityArgumentCaptor.capture(), anyBoolean())).thenReturn(Optional.of(userEntity));
        Mockito.when(userEntityService.makeUserEnabledByEmail(anyString())).thenReturn(true);
        Mockito.doNothing().when(emailService).deleteAccountVerificationByUserEntityPublicId(any(UUID.class));

        mockMvc
                .perform(post("/verify/{uuid}/new", uuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", password)
                        .param("repeatPassword", password))
                .andExpect(model().attributeExists("passwordReset"))
                .andExpect(model().attributeHasNoErrors("passwordReset"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertEquals(password, userEntityArgumentCaptor.getValue().getEncryptedPassword());

        Mockito.verify(emailService, Mockito.times(2)).findAccountVerificationByVerificationCode(any(UUID.class));
        Mockito.verify(userEntityService, Mockito.times(2)).findUserEntityByPublicId(any(UUID.class));
        Mockito.verify(userEntityService, Mockito.times(1)).saveOrUpdate(any(UserEntity.class), anyBoolean());
        Mockito.verify(userEntityService, Mockito.times(1)).makeUserEnabledByEmail(nullable(String.class));
        Mockito.verify(emailService, Mockito.times(1)).deleteAccountVerificationByUserEntityPublicId(any(UUID.class));
    }


    //Spring mockMvc doesn't consider validation @Valid - TEST NOT WORKING
    @Disabled
    @Test
    void processRegisterPage() throws Exception {
        UserEntity userEntity=new UserEntity();
        userEntity.setEmail("test@email");
        Mockito.when(userEntityService.saveOrUpdate(any(UserEntity.class), anyBoolean())).thenReturn(Optional.of(userEntity));
        Mockito.doNothing().when(emailService).sendVerificationEmail(anyString(), anyBoolean());

        mockMvc
                .perform(post("/register")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "") //should fail - but doesn't
                        .param("encryptedPassword", "pass")
                        .param("inputPasswordConfirm", "pass")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeHasNoErrors("user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?successRegistration"));

        Mockito.verify(userEntityService, Mockito.times(1)).saveOrUpdate(any(UserEntity.class), anyBoolean());
        Mockito.verify(emailService, Mockito.times(1)).sendVerificationEmail(nullable(String.class), anyBoolean());
    }

    //Spring mockMvc doesn't consider validation @Valid - TEST NOT WORKING
    @Disabled
    @DisplayName("processRegisterPage() invalid model attribute(UserEntity) fields input")
    @Test
    void processRegisterPage_invalidInput() throws Exception {
        // BindingResult gets only manually added errors(.rejectValue("fieldName", "error.user", "error message"))
        // in this case - inputPasswordConfirm is null
        // doesn't check firstName or any other fields.
        // @Valid annotation is ignored.

        mockMvc
                .perform(post("/register")
                        .with(csrf())
                        .param("firstName", "")
                        .param("encryptedPassword", "pass")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeHasErrors("user"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }
}