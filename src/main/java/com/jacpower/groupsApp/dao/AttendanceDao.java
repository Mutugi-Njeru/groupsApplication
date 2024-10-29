package com.jacpower.groupsApp.dao;

import com.jacpower.groupsApp.model.Attendance;
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
public class AttendanceDao {

    //attend meeting
    private final JdbcClient jdbcClient;
    private static final Logger logger= LoggerFactory.getLogger(AttendanceDao.class);

    @Autowired
    public AttendanceDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }
    //check if the meeting is open
    public boolean isMeetingOpen(int meetingId){
        String query="SELECT COUNT(meeting_id) FROM meeting WHERE meeting_id=? AND status=true";
        try {
            int count=jdbcClient.sql(query)
                    .param(meetingId)
                    .query((rs, rowNum)->rs.getInt(1))
                    .single();
            return count>0;
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }
    //member should attend meeting only once
    public boolean hasMemberAttendedMeeting(int meetingId, int memberId){
        String query="SELECT COUNT(member_id) FROM attendance WHERE meeting_id=? AND member_id=?";
        try {
            int count=jdbcClient.sql(query)
                    .param(meetingId)
                    .param(memberId)
                    .query((rs, rowNum)->rs.getInt(1))
                    .single();
            return count>0;
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }

    //add attendance
    public int addMeetingAttendance(Attendance attendance){
        String query="INSERT INTO attendance (meeting_id, member_id, presence) VALUES (?,?,?)";
        GeneratedKeyHolder generatedKeyHolder=new GeneratedKeyHolder();
        try {
            jdbcClient.sql(query)
                    .params(List.of(attendance.meetingId(), attendance.memberId(), attendance.presence()))
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }
    //get attendance details
    public List<JsonObject> getAttendanceDetails (int meetingId){
        String query= """
                SELECT m.firstname, m.lastname, a.meeting_id, a.amount, a.presence, a.member_id, a.attendance_id
                FROM attendance a
                INNER JOIN members m ON m.member_id=a.member_id
                WHERE a.meeting_id=?""";
        try {
           return jdbcClient.sql(query)
                    .param(meetingId)
                    .query((rs, rowNum)->{
                        JsonObjectBuilder jsonObjectBuilder= Json.createObjectBuilder()
                                .add("firstname", rs.getString(1))
                                .add("lastname", rs.getString(2))
                                .add("meetingId", rs.getInt(3))
                                .add("contribution", rs.getInt(4) )
                                .add("presence", rs.getString(5))
                                .add("memberId", rs.getInt(6))
                                .add("attendanceId", rs.getInt(7));
                        return jsonObjectBuilder.build();
                    }).stream().collect(Collectors.toList());
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }

    }

    //update contribution and appearance by attendance id
    public boolean updateAttendanceDetails(int amount, String presence, int attendanceId){
        String query="UPDATE attendance SET amount=?, presence=? WHERE attendance_id=?";
        try {
            int rowsUpdated=jdbcClient.sql(query)
                    .param(amount)
                    .param(presence)
                    .param(attendanceId)
                    .update();
            return rowsUpdated>0;
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw  e;
        }
    }




}

