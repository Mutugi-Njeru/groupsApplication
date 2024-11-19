package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.AccountDao;
import com.jacpower.groupsApp.dao.GroupDao;
import com.jacpower.groupsApp.dao.InventoryDao;
import com.jacpower.groupsApp.dao.UserDao;
import com.jacpower.groupsApp.model.Account;
import com.jacpower.groupsApp.model.DepositWithdrawDto;
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
    private final GroupDao groupDao;
    private final UserDao userDao;
    private final InventoryDao inventoryDao;

    @Autowired
    public AccountService(AccountDao accountDao, GroupDao groupDao, UserDao userDao, InventoryDao inventoryDao) {
        this.accountDao = accountDao;
        this.groupDao = groupDao;
        this.userDao = userDao;
        this.inventoryDao = inventoryDao;
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
    public ServiceResponder depositToAccount(DepositWithdrawDto dto){
        String fullName= userDao.getUserFullName(dto.userId()); //get username
        int groupId= groupDao.getGroupId(dto.userId());
        if (!fullName.isEmpty() && groupId>0){
            int inventoryId = inventoryDao.updateContributionToInventory(groupId, fullName, dto.amount(), dto.description(), true);
            if (inventoryId>0){
                int accountBalance= inventoryDao.getBalanceFromInventory(groupId);
                boolean balanceUpdated= accountDao.updateAccountBalance(accountBalance, groupId);
                return (balanceUpdated)
                        ? new ServiceResponder(HttpStatus.ACCEPTED, true, "deposit successful")
                        : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "operation failed");
            }
            else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot add record to inventory");

        }
        else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot get fullName or groupId");

    }

    public ServiceResponder withdrawFromAccount(DepositWithdrawDto dto){
        String fullName= userDao.getUserFullName(dto.userId());
        int groupId= groupDao.getGroupId(dto.userId());
        if (!fullName.isEmpty() && groupId>0){
            int inventoryId = inventoryDao.updateContributionToInventory(groupId, fullName, dto.amount(), dto.description(), false);
            if (inventoryId>0){
                int accountBalance= inventoryDao.getBalanceFromInventory(groupId);
                boolean balanceUpdated= accountDao.updateAccountBalance(accountBalance, groupId);
                return (balanceUpdated)
                        ? new ServiceResponder(HttpStatus.ACCEPTED, true, "withdraw successful")
                        : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "operation failed");
            }
            else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot add record to inventory");

        }
        else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot get fullName or groupId");
    }

}
