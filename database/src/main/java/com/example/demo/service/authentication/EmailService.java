package com.example.demo.service.authentication;

import com.example.demo.entity.Order;
import com.example.demo.entity.authentication.AccountVerification;
import com.example.demo.entity.authentication.UserEntity;

import javax.mail.MessagingException;
import javax.money.MonetaryAmount;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface EmailService {
    void sendVerificationEmail(String email, boolean resetPassword);
    boolean isAlreadyAccountVerificationByUserEmail(String email);
    void deleteAccountVerificationByUserEntityPublicId(UUID publicId);
    Optional<AccountVerification> findAccountVerificationByVerificationCode(UUID verificationCode);
    void sendOrderConfirmationEmail(String email, Order order, MonetaryAmount totalPrice);
}
