package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.AttendanceDao;
import com.jacpower.groupsApp.dao.MemberDao;
import com.jacpower.groupsApp.model.Attendance;
import com.jacpower.groupsApp.records.ServiceResponder;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceDao attendanceDao;
    private final MemberDao memberDao;

    @Autowired
    public AttendanceService(AttendanceDao attendanceDao, MemberDao memberDao) {
        this.attendanceDao = attendanceDao;
        this.memberDao = memberDao;
    }

    public ServiceResponder addAttendance(Attendance attendance) {
        boolean isMemberActive = memberDao.isMemberActive(attendance.memberId());
        if (isMemberActive) {
            boolean isMeetingOpen = attendanceDao.isMeetingOpen(attendance.meetingId());
            if (isMeetingOpen) {
                boolean isAttended = attendanceDao.hasMemberAttendedMeeting(attendance.meetingId(), attendance.memberId());
                if (!isAttended) {
                    int attendanceId = attendanceDao.addMeetingAttendance(attendance);
                    return (attendanceId > 0)
                            ? new ServiceResponder(HttpStatus.ACCEPTED, true, "attendance added successfully")
                            : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot add attendance");
                } else
                    return new ServiceResponder(HttpStatus.BAD_REQUEST, false, "you have already attended this meeting");

            } else
                return new ServiceResponder(HttpStatus.BAD_REQUEST, false, "The meeting is currently closed for attendance");
        } else
            return new ServiceResponder(HttpStatus.FORBIDDEN, false, "Your account is inactive. Contact system administrator");
    }

    public ServiceResponder getAttendanceDetails(int meetingId){
        List<JsonObject> attendanceDetails = attendanceDao.getAttendanceDetails(meetingId);
        JsonArray attendanceArray = Util.convertListToJsonArray(attendanceDetails);
        return (!attendanceArray.isEmpty())
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, attendanceArray)
                : new ServiceResponder(HttpStatus.NO_CONTENT, false, Json.createObjectBuilder().build());
    }
}
