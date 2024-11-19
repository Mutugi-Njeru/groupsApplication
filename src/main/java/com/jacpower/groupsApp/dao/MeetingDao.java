package com.jacpower.groupsApp.dao;

import com.jacpower.groupsApp.model.Meeting;
import com.jacpower.groupsApp.utility.Constants;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeetingDao {
    private final JdbcClient jdbcClient;
    private static final Logger logger = LoggerFactory.getLogger(MeetingDao.class);

    @Autowired
    public MeetingDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    //check if meeting's closed
    public boolean isOtherMeetingsClosed(int groupId) { //true means open meeting
        String query = "SELECT count(meeting_id) FROM meeting WHERE status=true and group_id=?";
        try {
            int count = jdbcClient.sql(query)
                    .param(groupId)
                    .query((rs, rowNum) -> rs.getInt(1))
                    .single();
            return count > 0;
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //create meeting--ensure meeting date is the future
    public int addMeeting(Meeting meeting) {
        String query = "INSERT INTO meeting (group_id, meeting_date, appearance, objective, location, status) VALUES (?,?,?,?,?,?)";

        try {
            Timestamp timestamp = Util.convertStringToSqlDate(meeting.meetingDate()); //convert meeting date string to sql date
            GeneratedKeyHolder generatedKeyHolder=new GeneratedKeyHolder();
            jdbcClient.sql(query)
                    .params(List.of(meeting.groupId(), timestamp, meeting.appearance(), meeting.objective(), meeting.location(), true))
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        } catch (ParseException e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw new RuntimeException(e);
        }
    }
    //get all meetings by userId
    public List<JsonObject> getGroupMeetings(int userId){
        String query= """
                SELECT m.meeting_Id, m.meeting_date, m.appearance, m.objective, m.location, m.status
                FROM meeting m
                INNER JOIN chama_group g ON m.group_id=g.group_id
                WHERE g.user_id=? AND g.is_active=true ORDER BY m.meeting_date DESC""";
        try {
            return jdbcClient.sql(query)
                    .param(userId)
                    .query((rs, rowNum)-> {
                        JsonObjectBuilder jsonObjectBuilder= Json.createObjectBuilder()
                                .add("meetingId", rs.getInt(1))
                                .add("meetingDate", String.valueOf(rs.getDate(2)))
                                .add("appearance", rs.getString(3))
                                .add("objective", rs.getString(4))
                                .add("location", rs.getString(5))
                                .add("status", rs.getBoolean(6));
                        return jsonObjectBuilder.build();
                    })
                    .stream().collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

    //close meeting
    public boolean closeMeeting(int meetingId){
        String query="UPDATE meeting SET status=false WHERE meeting_id=?";
        try {
            int rowsUpdated=jdbcClient.sql(query)
                    .param(meetingId)
                    .update();
            return rowsUpdated>0;
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }

}
