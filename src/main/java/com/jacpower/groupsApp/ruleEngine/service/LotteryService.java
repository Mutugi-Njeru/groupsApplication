package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.AccountDao;
import com.jacpower.groupsApp.dao.InventoryDao;
import com.jacpower.groupsApp.dao.LotteryDao;
import com.jacpower.groupsApp.dao.MemberDao;
import com.jacpower.groupsApp.model.Lottery;
import com.jacpower.groupsApp.records.ServiceResponder;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class LotteryService {
    private final LotteryDao lotteryDao;
    private final InventoryDao inventoryDao;
    private final AccountDao accountDao;
    private final MemberDao memberDao;

    @Autowired
    public LotteryService(LotteryDao lotteryDao, InventoryDao inventoryDao, AccountDao accountDao, MemberDao memberDao) {
        this.lotteryDao = lotteryDao;
        this.inventoryDao = inventoryDao;
        this.accountDao = accountDao;
        this.memberDao = memberDao;
    }

    public ServiceResponder addOrUpdateLottery(Lottery lottery){
        boolean isExists= lotteryDao.doesLotteryAmountExist(lottery.groupId()); //returns true if none
        if (isExists){
            int lotteryId = lotteryDao.addLotteryAmount(lottery.groupId(), lottery.amount());
            return (lotteryId>0)
                    ? new ServiceResponder(HttpStatus.ACCEPTED, true, "amount added successfully")
                    : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot add amount");
        }
        else {
            boolean isUpdated= lotteryDao.updateLotteryAmount(lottery.groupId(), lottery.amount());
            return (isUpdated)
                    ? new ServiceResponder(HttpStatus.ACCEPTED, true, "amount updated successfully")
                    : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot update amount");
        }
    }
    public ServiceResponder getLotteryWinner(int groupId){
        Set<Integer> memberIdsForGroup = lotteryDao.getMemberIdsForGroup(groupId);
        Set<Integer> memberIdsFromLotteryAwards = lotteryDao.getMemberIdsFromLotteryAwards(groupId);

        int lotteryWinnerId= Util.getMemberToAwardLottery(memberIdsForGroup, memberIdsFromLotteryAwards);
        if (lotteryWinnerId>0){
            Optional<JsonObject> lotteryWinner = lotteryDao.getLotteryWinner(lotteryWinnerId);
            return lotteryWinner.map(object -> new ServiceResponder(HttpStatus.ACCEPTED, true, object)).
                    orElseGet(() -> new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "Cannot get winner details"));
        }
        else return new ServiceResponder(HttpStatus.BAD_REQUEST, false, "cannot get lottery winner");
    }

    public ServiceResponder approveLottery(int memberId){
        int groupId = memberDao.getGroupIdByMemberId(memberId);
        String fullName = memberDao.getMemberFullName(memberId);
        int lotteryAmount = lotteryDao.getLotteryAmountByMemberId(memberId);
        int inventoryId = inventoryDao.updateContributionToInventory(groupId, fullName, lotteryAmount, "lottery", false);
        if (inventoryId>0){
            int lotteryId= lotteryDao.addLottery(memberId, lotteryAmount);
            int balanceFromInventory = inventoryDao.getBalanceFromInventory(groupId);
            boolean isUpdated = accountDao.updateAccountBalance(balanceFromInventory, groupId);
            return (lotteryId>0 && isUpdated)
                    ? new ServiceResponder(HttpStatus.ACCEPTED, true, "lottery approved successfully")
                    : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot approve lottery");
        }
        else return  new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot add lottery to inventory");
    }
    public ServiceResponder getLotterySummary(int groupId){
        List<JsonObject> lotteryWinnersSummary = lotteryDao.getLotteryWinnersSummary(groupId);
        JsonArray summary = Util.convertListToJsonArray(lotteryWinnersSummary);
        return (!summary.isEmpty())
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, summary)
                : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, Json.createArrayBuilder().build());
    }



}
