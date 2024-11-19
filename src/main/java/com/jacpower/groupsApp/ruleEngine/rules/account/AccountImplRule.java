package com.jacpower.groupsApp.ruleEngine.rules.account;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Account;
import com.jacpower.groupsApp.model.DepositWithdrawDto;
import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import com.jacpower.groupsApp.ruleEngine.service.AccountService;
import com.jacpower.groupsApp.utility.Constants;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class AccountImplRule implements ServiceRule {
    private final AccountService accountService;

    @Autowired
    public AccountImplRule(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.ACCOUNT.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Json.createReader(new StringReader(request.toString())).readObject();
        String requestType = requestBody.getString(Constants.REQUEST_TYPE, "");

        switch (RequestTypes.valueOf(requestType)){
            case DOES_ACCOUNT_EXIST:
                return Util.buildResponse(accountService.isAccountExist(requestBody.getInt("groupId")));
            case CREATE_ACCOUNT:
                return Util.buildResponse(accountService.createAccount(Account.fromJsonObject(requestBody)));
            case GET_ACCOUNT_DETAILS:
                return Util.buildResponse(accountService.getAccountDetails(requestBody.getInt("groupId")));
            case DEPOSIT_TO_ACCOUNT:
                return Util.buildResponse(accountService.depositToAccount(DepositWithdrawDto.fromJsonObject(requestBody)));
            case WITHDRAW_FROM_ACCOUNT:
                return Util.buildResponse(accountService.withdrawFromAccount(DepositWithdrawDto.fromJsonObject(requestBody)));
            default:
                throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }
    }
}
