package com.jacpower.groupsApp.dao;

import com.jacpower.groupsApp.model.AuthUser;
import com.jacpower.groupsApp.model.MyUser;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserDao {
    private final JdbcClient jdbcClient;
    private static final Logger logger= LoggerFactory.getLogger(UserDao.class);

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDao(JdbcClient jdbcClient, PasswordEncoder passwordEncoder) {
        this.jdbcClient = jdbcClient;
        this.passwordEncoder = passwordEncoder;
    }

    //check if user exists by email
    public boolean isUserExist(String email, String username) {
        String query = "SELECT COUNT(*) FROM user_details ud INNER JOIN users u ON u.user_id=ud.user_id WHERE ud.email=? OR u.username=?";

        try {
            Integer count = jdbcClient.sql(query)
                    .param(email)
                    .param(username)
                    .query((rs, rowNum) -> rs.getInt(1))
                    .single();
            logger.debug("Query result count: {}", count);
            return count > 0;
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            return false;
        }
    }

    // create user
    public int createUser (MyUser user){
        String query="INSERT INTO users (username, password, is_active) VALUES (?,?,?)";
        GeneratedKeyHolder generatedKeyHolder=new GeneratedKeyHolder();
        String hashedPassword= passwordEncoder.encode(user.password());

        try {
            jdbcClient.sql(query)
                    .params(List.of(user.username(), hashedPassword, true))
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }

    //add user details
    public int addUserDetails (int userId, MyUser user){
        String query="INSERT INTO user_details (user_id, firstname, lastname, phone_number, email) VALUES (?,?,?,?,?)";
        GeneratedKeyHolder generatedKeyHolder=new GeneratedKeyHolder();
        try {
            jdbcClient.sql(query)
                    .params(List.of(userId, user.firstname(), user.lastname(), user.phoneNumber(), user.email()))
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }

    //get user by username
    public Optional<AuthUser> getUser (String username){
        String query= " SELECT u.username, u.password FROM users u WHERE u.username=? AND u.is_active=true";
        try {
            return jdbcClient.sql(query)
                    .param(username)
                    .query(AuthUser.class)
                    .optional();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }
    //get userid by username
    public int getUserId(String username){
        String query="SELECT user_id FROM users WHERE username=?";
        try {
            return jdbcClient.sql(query)
                    .param(username)
                    .query((rs, rowNum)-> rs.getInt(1))
                    .single();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }

    public  String getUserFullName(int userId){
        String query="SELECT concat(firstname, ' ', lastname) as fullName from user_details where user_id=?";
        try {
            return jdbcClient.sql(query)
                    .param(userId)
                    .query((rs, rowNum)-> rs.getString(1))
                    .single();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }

    }
    //update username and password
    public boolean updateUsernameAndPassword(int userId, String username, String password){
        String query="UPDATE users SET username=?, password=? WHERE user_id=?";
        String hashedPassword=passwordEncoder.encode(password);
        try {
            int rowsUpdated=jdbcClient.sql(query)
                    .param(username)
                    .param(hashedPassword)
                    .param(userId)
                    .update();
            return rowsUpdated>0;
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }

    //get all users



















}
