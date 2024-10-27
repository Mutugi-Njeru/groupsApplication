package com.jacpower.groupsApp.dao;

import com.jacpower.groupsApp.model.Group;
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
public class GroupDao {
    private final JdbcClient jdbcClient;
    private final Logger logger= LoggerFactory.getLogger(GroupDao.class);
    @Autowired
    public GroupDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    //check if group exists by registration pin and group name

    public boolean isGroupExist(String registrationPin, String groupName, String email){
        String query="SELECT COUNT(group_id) FROM chama_group WHERE registration_pin=? OR group_name=? OR email_address=?";
        try {
            int count=jdbcClient.sql(query)
                    .params(List.of(registrationPin, groupName, email))
                    .query((rs, rowNum)-> rs.getInt(1))
                    .single();
            return count>0;
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }

    //create group
    public int createGroup (Group group){
        String query="INSERT INTO chama_group (user_id, group_name, email_address, registration_pin, address, phone_number, group_description, is_active) VALUES (?,?,?,?,?,?,?,?)";
        GeneratedKeyHolder generatedKeyHolder=new GeneratedKeyHolder();
        try {
            jdbcClient.sql(query)
                    .params(List.of(group.userId(), group.groupName(), group.emailAddress(), group.registrationPin(), group.address(), group.phoneNumber(), group.groupDescription(), true))
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }
    //get group details by userId
    public Optional<JsonObject> getGroupDetails(int userId){
        String query= """
                SELECT JSON_OBJECT(
                'groupId', group_id,
                'groupName', group_name,
                'emailAddress', email_address,
                'registrationPin', registration_pin,
                'address', address,
                'phoneNumber', phone_number,
                'groupDescription', group_description,
                'isActive', is_active
                )
                AS group_json FROM chama_group WHERE user_id=?
                """;
        return jdbcClient.sql(query)
                .param(userId)
                .query((rs, rowNum) -> Json.createReader(new StringReader(rs.getString("group_json"))).readObject())
                .optional();
    }

    //update group
    public boolean updateGroupDetails(Group group){
        String query="UPDATE chama_group SET group_name=?, email_address=?, registration_pin=?, address=?, phone_number=?, group_description=? WHERE user_id=?";
        try {
            int rowsUpdated=jdbcClient.sql(query)
                    .params(List.of(group.groupName(), group.emailAddress(), group.registrationPin(), group.address(), group.phoneNumber(), group.groupDescription(), group.userId()))
                    .update();
            return rowsUpdated>0;
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }
    //get groupId by userId
    public int getGroupId(int userId){
        String query="SELECT group_id FROM chama_group WHERE user_id=?";
        try {
            return jdbcClient.sql(query)
                    .param(userId)
                    .query((rs, rowNum)->rs.getInt(1))
                    .single();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }
     //get groupName by groupId
    public String getGroupName(int groupId){
        String query="SELECT group_name FROM chama_group WHERE group_id=?";
        try {
            return jdbcClient.sql(query)
                    .param(groupId)
                    .query((rs, rowNum)->rs.getString(1))
                    .single();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }


}














