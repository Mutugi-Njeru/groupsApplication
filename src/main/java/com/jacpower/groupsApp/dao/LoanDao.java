package com.jacpower.groupsApp.dao;

import com.jacpower.groupsApp.model.Loan;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class LoanDao {
    private final JdbcClient jdbcClient;
    private static final Logger logger = LoggerFactory.getLogger(LoanDao.class);

    @Autowired
    public LoanDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    //check if member has pending unpaid loan
    public boolean isLoanPaid(int memberId) {
        String query = "SELECT COUNT(*) FROM loans WHERE status!='denied' AND paid=false AND member_id=?";
        try {
            int count = jdbcClient.sql(query)
                    .param(memberId)
                    .query((rs, rowNum) -> rs.getInt(1))
                    .single();
            return count == 1;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    // add loan request
    public int requestLoan(Loan loan) {
        String query = "INSERT INTO loans (member_id, amount, status, paid) VALUES (?,?,?,?)";
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        try {
            jdbcClient.sql(query)
                    .param(loan.memberId())
                    .param(loan.amount())
                    .param("waiting")
                    .param(false)
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //check if amount being borrowed is enough
    public boolean isAmountEnough(int groupId, int amount) {
        String query = "SELECT COUNT(*) FROM account where amount>? and group_id=?";
        try {
            int count = jdbcClient.sql(query)
                    .param(amount)
                    .param(groupId)
                    .query((rs, rowNum) -> rs.getInt(1))
                    .single();
            return count == 1;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //update loan status to approved
    public boolean updateLoanStatus(int loanId, String status) {
        String query = "UPDATE loans SET status=? WHERE load_id=? AND status='waiting'";
        try {
            int rowsUpdated = jdbcClient.sql(query)
                    .param(status)
                    .param(loanId)
                    .update();
            return rowsUpdated > 0;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    public int getLoanRequestAmount(int memberId, String status, boolean isPaid) {
        String query = "SELECT amount FROM loans WHERE member_id=? AND status=? AND paid=?";
        try {
            return jdbcClient.sql(query)
                    .param(memberId)
                    .param(status)
                    .param(isPaid)
                    .query((rs, rowNum) -> rs.getInt(1))
                    .single();
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    public int getWaitingLoanId(int memberId) {
        String query = "SELECT load_id FROM loans WHERE member_id=? AND status='waiting'";
        try {
            return jdbcClient.sql(query)
                    .param(memberId)
                    .query((rs, rowNum) -> rs.getInt(1))
                    .single();
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    public int getApprovedUnpaidLoanId(int memberId) {
        String query = "SELECT load_id FROM loans WHERE member_id=? AND status='approved' AND paid=false";
        try {
            return jdbcClient.sql(query)
                    .param(memberId)
                    .query((rs, rowNum) -> rs.getInt(1))
                    .single();
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    public List<JsonObject> getLoans(int groupId) {
        String query = """
                SELECT CONCAT(m.Firstname, ' ', m.lastname) AS fullName, m.id_number, m.phone, l.amount, l.status, l.paid, l.member_id
                FROM members m
                INNER JOIN loans l ON m.member_id=l.member_id
                INNER JOIN chama_group c ON c.group_id=m.group_id
                WHERE m.group_id=? AND c.is_active=true ORDER BY l.created_at DESC""";
        try {
            return jdbcClient.sql(query)
                    .param(groupId)
                    .query((rs, rowNum) -> {
                        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                                .add("fullName", rs.getString(1))
                                .add("idNumber", rs.getString(2))
                                .add("phoneNumber", rs.getString(3))
                                .add("amount", rs.getInt(4))
                                .add("status", rs.getString(5))
                                .add("paid", rs.getBoolean(6))
                                .add("memberId", rs.getInt(7));
                        return jsonObjectBuilder.build();
                    }).stream().collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //repay loan
    public boolean updateLoanPaidStatus(int loanId) {
        String query = "UPDATE loans SET paid=true WHERE load_id=?";
        try {
            int rowsUpdated = jdbcClient.sql(query)
                    .param(loanId)
                    .update();
            return rowsUpdated > 0;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }


}
