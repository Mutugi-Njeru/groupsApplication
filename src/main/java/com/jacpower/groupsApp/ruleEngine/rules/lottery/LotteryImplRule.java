package com.jacpower.groupsApp.ruleEngine.rules.lottery;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Lottery;
import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import com.jacpower.groupsApp.ruleEngine.service.LotteryService;
import com.jacpower.groupsApp.utility.Constants;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class LotteryImplRule implements ServiceRule {
    private final LotteryService lotteryService;

    @Autowired
    public LotteryImplRule(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.LOTTERY.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Json.createReader(new StringReader(request.toString())).readObject();
        String requestType = requestBody.getString(Constants.REQUEST_TYPE, "");

        switch (RequestTypes.valueOf(requestType)){
            case ADD_UPDATE_LOTTERY:
                return Util.buildResponse(lotteryService.addOrUpdateLottery(Lottery.fromJsonObject(requestBody)));
            case GET_WINNER:
                return Util.buildResponse(lotteryService.getLotteryWinner(requestBody.getInt("groupId")));
            case APPROVE_LOTTERY:
                return Util.buildResponse(lotteryService.approveLottery(requestBody.getInt("memberId")));
            case GET_LOTTERY_SUMMARY:
                return Util.buildResponse(lotteryService.getLotterySummary(requestBody.getInt("groupId")));
            default:
                throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }
    }
}
