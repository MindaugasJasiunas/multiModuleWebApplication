package com.example.demo.entity.authentication;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor

@Table(name = "account_verification")
@Entity
public class AccountVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "user_entity_public_id",unique = true)
    private UUID userEntityPublicId;
    @Column(name = "verification_code", unique = true)
    private UUID verificationCode=UUID.randomUUID();
    private boolean resetPassword;

    public AccountVerification(UUID userEntityPublicId, boolean resetPassword) {
        this.userEntityPublicId = userEntityPublicId;
        this.resetPassword = resetPassword;
    }
}
