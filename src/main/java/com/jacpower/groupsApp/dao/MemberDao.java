package com.jacpower.groupsApp.dao;

import com.jacpower.groupsApp.model.Member;
import com.jacpower.groupsApp.model.MemberDto;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.io.StringReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MemberDao {
    private final JdbcClient jdbcClient;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(MemberDao.class);

    @Autowired
    public MemberDao(JdbcClient jdbcClient, PasswordEncoder passwordEncoder) {
        this.jdbcClient = jdbcClient;
        this.passwordEncoder = passwordEncoder;
    }

    //check if member exists
    public boolean isMemberExist(String idNumber, String email) {
        String query = "SELECT COUNT(member_id) FROM members WHERE id_number=? OR email=?";
        try {
            int count = jdbcClient.sql(query)
                    .params(List.of(idNumber, email))
                    .query((rs, rowNum) -> rs.getInt(1))
                    .single();
            return count > 0;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    // add into members
    public int createMember(Member member) {
        String query = "INSERT INTO members (group_id, Firstname, lastname, phone, id_number, email, is_active) VALUES (?,?,?,?,?,?,?)";
        try {
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            jdbcClient.sql(query)
                    .params(List.of(member.groupId(), member.firstname(), member.lastname(), member.phoneNumber(), member.idNumber(), member.email(), true))
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //check no user exists with the username
    public boolean isUsernameTaken(String username) {
        String query = "SELECT COUNT(user_id) FROM users WHERE username=?";
        try {
            int count = jdbcClient.sql(query)
                    .param(username)
                    .query((rs, rowNum) -> rs.getInt(1))
                    .single();
            return count > 0;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //add into users to get user_id
    public int addMemberToUsers(String username, String password) {
        String query = "INSERT INTO users (username, password, is_active) VALUES (?,?,?)";
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String hashedPassword = passwordEncoder.encode(password);

        try {
            jdbcClient.sql(query)
                    .params(List.of(username, hashedPassword, true))
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //get group members  by groupId
    public List<JsonObject> getGroupMembers(int groupId) {
        String query = """
                    SELECT m.member_id, CONCAT(m.firstname, ' ', m.lastname) AS full_name, m.phone, m.id_number, m.email, m.is_active
                    FROM members m WHERE m.group_id=?;
                """;
        try {
            return jdbcClient.sql(query)
                    .param(groupId)
                    .query((rs, rowNum) -> {
                        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                                .add("memberId", rs.getInt(1))
                                .add("fullName", rs.getString(2))
                                .add("phone", rs.getString(3))
                                .add("idNumber", rs.getString(4))
                                .add("email", rs.getString(5))
                                .add("status", rs.getBoolean(6));
                        return jsonObjectBuilder.build();
                    })
                    .stream().collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //get member by member id
    public Optional<JsonObject> getMemberByMemberId(int memberId) {
        String query = """
                 SELECT JSON_OBJECT(
                'firstname', Firstname,
                'lastname', lastname,
                'phone', phone,
                'idNumber', id_number,
                'email', email)
                AS member_json FROM
                members WHERE member_id=?
                 """;
        try {
            return jdbcClient.sql(query)
                    .param(memberId)
                    .query((rs, rowNum) -> Json.createReader(new StringReader(rs.getString("member_json"))).readObject())
                    .optional();
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //update member by member id
    public boolean updateMemberDetails(MemberDto member, int memberId) {
        String query = "UPDATE members SET Firstname=?, lastname=?, id_number=?, phone=?, email=? WHERE member_id=?";
        try {
            int rowUpdated = jdbcClient.sql(query)
                    .params(List.of(member.firstname(), member.lastname(), member.idNumber(), member.phone(), member.email(), memberId))
                    .update();
            return rowUpdated > 0;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //deactivate member in members
    public boolean deactivateMember(int memberId) {
        String query = "UPDATE members SET is_active=false WHERE member_id=?";
        try {
            int rowUpdated = jdbcClient.sql(query)
                    .param(memberId)
                    .update();
            return rowUpdated > 0;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    public boolean activateMember(int memberId) {
        String query = "UPDATE members SET is_active=true WHERE member_id=?";
        try {
            int rowUpdated = jdbcClient.sql(query)
                    .param(memberId)
                    .update();
            return rowUpdated > 0;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //get members emails
    public List<JsonObject> getMemberEmails(int groupId) {
        String query = "SELECT email FROM members WHERE group_id=? AND is_active=true";
        try {
            return jdbcClient.sql(query)
                    .param(groupId)
                    .query((rs, rowNum) -> {
                        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                                .add("email", rs.getString(1));
                        return jsonObjectBuilder.build();
                    })
                    .stream().collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //check if member is active
    public boolean isMemberActive(int memberId){
        String query="SELECT COUNT(member_id) FROM members WHERE member_id=? AND is_active=true";
        try {
            int count=jdbcClient.sql(query)
                    .param(memberId)
                    .query((rs, rowNum)-> rs.getInt(1))
                    .single();
            return count>0;
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }
    //get fullName by memberId
    public String getMemberFullName(int memberId){
        String query="SELECT CONCAT(Firstname, ' ', lastname) AS fullName FROM members WHERE member_id=?";
        try {
            return jdbcClient.sql(query)
                    .param(memberId)
                    .query((rs, rowNum)-> rs.getString(1))
                    .single();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }

    }

}
