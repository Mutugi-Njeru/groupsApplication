package com.jacpower.groupsApp.dao;

import com.jacpower.groupsApp.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class InventoryDao {
    private final JdbcClient jdbcClient;
    private static final Logger logger= LoggerFactory.getLogger(InventoryDao.class);

    @Autowired
    public InventoryDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    //create inventory
    public int updateContributionToInventory(int groupId, String fullName, int amount){
        String query="INSERT INTO inventory (group_id, full_name, description, amount, status) VALUES (?,?,?,?,?)";
        GeneratedKeyHolder generatedKeyHolder=new GeneratedKeyHolder();
        try{
            jdbcClient.sql(query)
                    .params(List.of(groupId, fullName, "contribution", amount, true))
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }
    public int getBalanceFromInventory(int groupId){
        String query= """
                SELECT
                    SUM(CASE WHEN status = true THEN amount ELSE 0 END) -
                    SUM(CASE WHEN status = false THEN amount ELSE 0 END) AS difference
                FROM inventory WHERE group_id =?""";
        try {
            return jdbcClient.sql(query)
                    .param(groupId)
                    .query((rs, rowNum)-> rs.getInt(1))
                    .single();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }

    }
}
