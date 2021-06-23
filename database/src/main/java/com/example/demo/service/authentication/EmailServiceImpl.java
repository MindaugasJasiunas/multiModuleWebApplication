package com.example.demo.service.authentication;

import com.example.demo.dao.authentication.AccountVerificationRepository;
import com.example.demo.entity.authentication.AccountVerification;
import com.example.demo.entity.authentication.UserEntity;
import com.example.demo.service.UserEntityService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService{
    private final AccountVerificationRepository accountVerificationRepo;
    private final UserEntityService userEntityService;

    public EmailServiceImpl(AccountVerificationRepository accountVerificationRepo, UserEntityService userEntityService) {
        this.accountVerificationRepo = accountVerificationRepo;
        this.userEntityService = userEntityService;
    }

    @Override
    public void sendVerificationEmail(String email, boolean resetPassword){
        System.out.println("sendVerificationEmail("+email+","+resetPassword+")");
        //make DB entry
        UserEntity userEntity= userEntityService.findUserEntityByEmail(email).get();
        AccountVerification verification=new AccountVerification(userEntity.getPublicId(), resetPassword);
        if(!isAlreadyAccountVerificationByUserEmail(email)){
            accountVerificationRepo.save(verification);
        }
        // send email with code & link
    }

    @Override
    public boolean isAlreadyAccountVerificationByUserEmail(String email){
        if(userEntityService.findUserEntityByEmail(email).isPresent()){
            UserEntity user= userEntityService.findUserEntityByEmail(email).get();
            if(accountVerificationRepo.findAccountVerificationByUserEntityPublicId(user.getPublicId()).isPresent()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteAccountVerificationByUserEntityPublicId(UUID userEntityPublicId){
        if(userEntityService.findUserEntityByPublicId(userEntityPublicId).isPresent()){
            accountVerificationRepo.deleteAccountVerificationByUserEntityPublicId(userEntityPublicId);
        }
    }

    @Override
    public Optional<AccountVerification> findAccountVerificationByVerificationCode(UUID verificationCode){
        return accountVerificationRepo.findAccountVerificationByVerificationCode(verificationCode);
    }






}
