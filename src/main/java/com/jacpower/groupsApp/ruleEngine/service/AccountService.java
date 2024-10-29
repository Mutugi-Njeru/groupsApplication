package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.AccountDao;
import com.jacpower.groupsApp.model.Account;
import com.jacpower.groupsApp.records.ServiceResponder;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private final AccountDao accountDao;

    @Autowired
    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public ServiceResponder isAccountExist(int groupId) {
        boolean isExist = accountDao.doesAccountExist(groupId);//returns false if account does not exist
        return new ServiceResponder(HttpStatus.ACCEPTED, true, Json.createObjectBuilder().add("status", isExist).build());
    }

    public ServiceResponder createAccount(Account account){
        boolean isExist= accountDao.doesAccountExist(account.groupId());
        if (!isExist){
            int accountId= accountDao.createAccount(account);
            return (accountId>0)
                    ? new ServiceResponder(HttpStatus.ACCEPTED, true, "account created successfully")
                    : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot create account");
        }
        else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "Account already Exists");
    }

    public ServiceResponder getAccountDetails(int groupId){
        Optional<JsonObject> accountDetails = accountDao.getAccountDetails(groupId);
        if (accountDetails.isPresent()){
            JsonObject group=accountDetails.get();
            return (!group.isEmpty())
                    ? new ServiceResponder(HttpStatus.ACCEPTED, true, group)
                    :new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, Json.createObjectBuilder().build());
        }
        else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, Json.createObjectBuilder().build());
    }

}
