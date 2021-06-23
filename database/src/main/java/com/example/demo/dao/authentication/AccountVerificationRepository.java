package com.example.demo.dao.authentication;

import com.example.demo.entity.authentication.AccountVerification;
import com.example.demo.entity.authentication.Authority;
import com.example.demo.entity.authentication.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountVerificationRepository extends JpaRepository<AccountVerification, Long> {
    Optional<AccountVerification> findAccountVerificationByUserEntityPublicId(UUID userEntitypublicId);
    Optional<AccountVerification> findAccountVerificationByVerificationCode(UUID verificationCode);

    @Transactional
    @Modifying
    void deleteAccountVerificationByUserEntityPublicId(UUID userEntityPublicId);

}
