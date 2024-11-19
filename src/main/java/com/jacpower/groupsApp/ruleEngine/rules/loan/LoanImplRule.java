package com.jacpower.groupsApp.ruleEngine.rules.loan;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Loan;
import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import com.jacpower.groupsApp.ruleEngine.service.LoanService;
import com.jacpower.groupsApp.utility.Constants;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class LoanImplRule implements ServiceRule {
    private final LoanService loanService;

    @Autowired
    public LoanImplRule(LoanService loanService) {
        this.loanService = loanService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.LOAN.name()));
    }
    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Json.createReader(new StringReader(request.toString())).readObject();
        String requestType = requestBody.getString(Constants.REQUEST_TYPE, "");

        switch (RequestTypes.valueOf(requestType)){
            case REQUEST_LOAN:
                return Util.buildResponse(loanService.requestLoan(Loan.fromJsonObject(requestBody)));
            case APPROVE_LOAN:
                return Util.buildResponse(loanService.approveLoan(requestBody.getInt("memberId")));
            case GET_LOANS:
                return Util.buildResponse(loanService.getLoans(requestBody.getInt("groupId")));
            case DENY_LOAN:
                return Util.buildResponse(loanService.denyLoan(requestBody.getInt("memberId")));
            case REPAY_LOAN:
                return Util.buildResponse(loanService.repayLoan(requestBody.getInt("memberId")));
            default:
                throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }

    }















}
