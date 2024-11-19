package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.AccountDao;
import com.jacpower.groupsApp.dao.InventoryDao;
import com.jacpower.groupsApp.dao.LoanDao;
import com.jacpower.groupsApp.dao.MemberDao;
import com.jacpower.groupsApp.model.Loan;
import com.jacpower.groupsApp.records.ServiceResponder;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoanService {
    private final LoanDao loanDao;
    private final MemberDao memberDao;
    private final InventoryDao inventoryDao;
    private final AccountDao accountDao;

    @Autowired
    public LoanService(LoanDao loanDao, MemberDao memberDao, InventoryDao inventoryDao, AccountDao accountDao) {
        this.loanDao = loanDao;
        this.memberDao = memberDao;
        this.inventoryDao = inventoryDao;
        this.accountDao = accountDao;
    }

    public ServiceResponder requestLoan(Loan loan) {
        boolean isActive = memberDao.isMemberActive(loan.memberId());
        if (isActive) {
            boolean isLoanPaid = loanDao.isLoanPaid(loan.memberId());
            if (!isLoanPaid) {
                int loanId = loanDao.requestLoan(loan);
                return (loanId > 0)
                        ? new ServiceResponder(HttpStatus.ACCEPTED, true, "loan requested. Please wait approval")
                        : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot request loan");
            } else return new ServiceResponder(HttpStatus.FORBIDDEN, false, "Your have unpaid loan or waiting approval");

        } else return new ServiceResponder(HttpStatus.FORBIDDEN, false, "Your account is inactive");
    }

    @Transactional
    public ServiceResponder approveLoan( int memberId) {
        int groupId = memberDao.getGroupIdByMemberId(memberId);
        int amount=loanDao.getLoanRequestAmount(memberId, "waiting", false);
        int loanId= loanDao.getWaitingLoanId(memberId);
        boolean isBalanceEnough = loanDao.isAmountEnough(groupId, amount);
        if (isBalanceEnough) {
            String fullName = memberDao.getMemberFullName(memberId);
            int inventoryId = inventoryDao.updateContributionToInventory(groupId, fullName, amount, "loan", false);

            boolean isApproved = loanDao.updateLoanStatus(loanId, "approved");
            if (inventoryId > 0 && isApproved) {
                int balanceFromInventory = inventoryDao.getBalanceFromInventory(groupId);
                boolean isUpdated = accountDao.updateAccountBalance(balanceFromInventory, groupId);
                return (isUpdated)
                        ? new ServiceResponder(HttpStatus.ACCEPTED, true, "loan approved successfully")
                        : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot approve loan");
            } else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot update inventory");
        } else {
            boolean isDenied = loanDao.updateLoanStatus(loanId, "denied");
            return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, !isDenied, "Sorry. Insufficient balance to give loan");
        }
    }

    public ServiceResponder getLoans(int groupId){
        List<JsonObject> loans = loanDao.getLoans(groupId);
        JsonArray loansDetails = Util.convertListToJsonArray(loans);
        return (!loansDetails.isEmpty())
                ? new ServiceResponder(HttpStatus.OK, true, loansDetails)
                : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, Json.createArrayBuilder().build());
    }

    public ServiceResponder denyLoan(int memberId){
        int loanId= loanDao.getWaitingLoanId(memberId);
        boolean isDenied = loanDao.updateLoanStatus(loanId, "denied");
        return (isDenied)
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, "loan denied successfully")
                : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "operation failed");
    }

    public ServiceResponder repayLoan( int memberId){
        int groupId = memberDao.getGroupIdByMemberId(memberId);
        String fullName = memberDao.getMemberFullName(memberId);
        int amount=loanDao.getLoanRequestAmount(memberId, "approved", false);
        int inventoryId = inventoryDao.updateContributionToInventory(groupId, fullName, amount, "loan", true);
        if (inventoryId> 0){
            int loanId= loanDao.getApprovedUnpaidLoanId(memberId);
            boolean isUpdated = loanDao.updateLoanPaidStatus(loanId);
            if (isUpdated){
                int balanceFromInventory = inventoryDao.getBalanceFromInventory(groupId);
                boolean isAccountUpdated = accountDao.updateAccountBalance(balanceFromInventory, groupId);
                return (isAccountUpdated)
                        ? new ServiceResponder(HttpStatus.ACCEPTED, true, "loan repaid successfully")
                        : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot repay loan");
            }
            else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot update loan to paid");
        }
        else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot update inventory");
    }


}
