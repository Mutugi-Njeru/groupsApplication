package com.jacpower.groupsApp.dao;

import com.jacpower.groupsApp.model.Account;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.io.StringReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class AccountDao {
    private final JdbcClient jdbcClient;
    private static final Logger logger= LoggerFactory.getLogger(AccountDao.class);

    @Autowired
    public AccountDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    //check if group has an account
    public boolean doesAccountExist(int groupId){
        String query="SELECT COUNT(account_id) FROM account WHERE group_id=?";
        try {
            int count=jdbcClient.sql(query)
                    .param(groupId)
                    .query((rs, rowNum)-> rs.getInt(1))
                    .single();
            return count>0;
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //create account
    public int createAccount(Account account){
        String query="INSERT INTO account(group_id, account_name) VALUES (?,?)";
        GeneratedKeyHolder generatedKeyHolder=new GeneratedKeyHolder();
        try {
            jdbcClient.sql(query)
                    .params(List.of(account.groupId(), account.accountName()))
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }
    public Optional<JsonObject> getAccountDetails(int groupId){
        String query= """
                SELECT JSON_OBJECT(
                'accountId', account_id,
                'accountName', account_name,
                'amount', amount)
                AS account_json FROM account
                WHERE group_id=?
                """;
        try {
            return jdbcClient.sql(query)
                    .param(groupId)
                    .query((rs, rowNum) -> Json.createReader(new StringReader(rs.getString("account_json"))).readObject())
                    .optional();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }
    public boolean updateAccountBalance(int amount, int groupId){
        String query="UPDATE account SET amount=? WHERE group_id=?";
        try {
            int rowsUpdated=jdbcClient.sql(query)
                    .param(amount)
                    .param(groupId)
                    .update();
            return rowsUpdated>0;
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }

}
