package com.jacpower.groupsApp.dao;

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

import java.io.StringReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InventoryDao {
    private final JdbcClient jdbcClient;
    private static final Logger logger= LoggerFactory.getLogger(InventoryDao.class);

    @Autowired
    public InventoryDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public int updateContributionToInventory(int groupId, String fullName, int amount, String description, boolean status){
        String query="INSERT INTO inventory (group_id, full_name, description, amount, status) VALUES (?,?,?,?,?)";
        GeneratedKeyHolder generatedKeyHolder=new GeneratedKeyHolder();
        try{
            jdbcClient.sql(query)
                    .params(List.of(groupId, fullName, description, amount, status))
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
    public List<JsonObject> getInventoryDetails(int groupId){
        String query= """
                SELECT i.created_at, i.full_name, i.description, i.amount, i.status
                FROM inventory i
                INNER JOIN chama_group c ON i.group_id=c.group_id
                WHERE i.group_id=? AND c.is_active=true ORDER BY i.created_at DESC""";
        try {
            return jdbcClient.sql(query)
                    .param(groupId)
                    .query((rs, rowNum)->{
                        JsonObjectBuilder jsonObjectBuilder=Json.createObjectBuilder()
                                .add("createdAt", String.valueOf(rs.getTimestamp(1)))
                                .add("fullName", rs.getString(2))
                                .add("description", rs.getString(3))
                                .add("amount", rs.getInt(4))
                                .add("status", rs.getBoolean(5));
                        return jsonObjectBuilder.build();
                    }).stream().collect(Collectors.toList());
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }


















}
