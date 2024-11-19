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
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class LotteryDao {
    private final JdbcClient jdbcClient;
    private static final Logger logger = LoggerFactory.getLogger(LotteryDao.class);

    @Autowired
    public LotteryDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    //check if amount exists
    //if exists update the amount
    //if no exist create new record
    public boolean doesLotteryAmountExist(int groupId) {
        String query = "SELECT COUNT(*) FROM lottery WHERE group_id=?";
        try {
            int count = jdbcClient.sql(query)
                    .param(groupId)
                    .query((rs, rowNum) -> rs.getInt(1))
                    .single();
            return count == 0;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    public boolean updateLotteryAmount(int groupId, int amount) {
        String query = "UPDATE lottery SET amount=? WHERE group_id=?";
        try {
            int rowsUpdated = jdbcClient.sql(query)
                    .param(amount)
                    .param(groupId)
                    .update();
            return rowsUpdated > 0;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    public int addLotteryAmount(int groupId, int amount) {
        String query = "INSERT INTO lottery (group_id, amount) VALUES (?,?)";
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        try {
            jdbcClient.sql(query)
                    .param(groupId)
                    .param(amount)
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    public Set<Integer> getMemberIdsForGroup(int groupId) {
        String query = "SELECT member_id FROM members WHERE group_id=? AND is_active=true";
        return jdbcClient.sql(query)
                .param(groupId)
                .query(Integer.class)
                .set();
    }

    public Set<Integer> getMemberIdsFromLotteryAwards(int groupId) {
        String query = "SELECT DISTINCT l.member_id FROM lottery_winners l INNER JOIN members m ON m.member_id=l.member_id WHERE m.group_id=?";
        return jdbcClient.sql(query)
                .param(groupId)
                .query(Integer.class)
                .set();
    }

    public Optional<JsonObject> getLotteryWinner(int memberId) {
        String query = """
                SELECT JSON_OBJECT (
                'firstname', m.Firstname, 
                'lastname', m.lastname, 
                'idNumber', m.id_number, 
                'phone', m.phone, 
                'amount', l.amount,
                'memberId', m.member_id
                ) AS winner_json
                FROM members m
                INNER JOIN lottery l ON l.group_id=m.group_id
                INNER JOIN account a ON a.group_id=m.group_id
                 WHERE a.amount > l.amount AND m.member_id=?
                """;
        try {
            return jdbcClient.sql(query)
                    .param(memberId)
                    .query((rs, rowNum) -> Json.createReader(new StringReader(rs.getString("winner_json"))).readObject())
                    .optional();
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }


    public int addLottery(int memberId, int amount) {
        String query = "INSERT INTO lottery_winners (member_id, amount) VALUES (?,?)";
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        try {
            jdbcClient.sql(query)
                    .param(memberId)
                    .param(amount)
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    public int getLotteryAmountByMemberId(int memberId) {
        String query = "SELECT l.amount FROM lottery l INNER JOIN members m ON m.group_id=l.group_id WHERE m.member_id=?";
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

    public List<JsonObject> getLotteryWinnersSummary(int groupId) {
        String query = """
                SELECT  CONCAT(m.firstname, ' ', m.lastname) as fullName, m.id_number, m.phone, l.amount, l.created_at
                FROM lottery_winners l
                INNER JOIN members m ON m.member_id=l.member_id
                INNER JOIN chama_group c ON c.group_id=m.group_id
                WHERE m.group_id=? AND c.is_active=true ORDER BY l.created_at desc""";
        try {
            return jdbcClient.sql(query)
                    .param(groupId)
                    .query((rs, rowNum) -> {
                        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                                .add("fullName", rs.getString(1))
                                .add("idNumber", rs.getString(2))
                                .add("phone", rs.getString(3))
                                .add("amount", rs.getInt(4))
                                .add("date", String.valueOf(rs.getTimestamp(5)));
                        return jsonObjectBuilder.build();
                    }).stream().collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

}
