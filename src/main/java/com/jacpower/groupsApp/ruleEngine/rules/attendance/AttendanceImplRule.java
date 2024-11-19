package com.jacpower.groupsApp.ruleEngine.rules.attendance;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Attendance;
import com.jacpower.groupsApp.model.AttendanceUpdateDto;
import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import com.jacpower.groupsApp.ruleEngine.service.AttendanceService;
import com.jacpower.groupsApp.utility.Constants;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class AttendanceImplRule implements ServiceRule {
    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceImplRule(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.ATTENDANCE.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Json.createReader(new StringReader(request.toString())).readObject();
        String requestType = requestBody.getString(Constants.REQUEST_TYPE, "");

        switch (RequestTypes.valueOf(requestType)){
            case ADD_ATTENDANCE:
                return Util.buildResponse(attendanceService.addAttendance(Attendance.fromJsonObject(requestBody)));
            case GET_ATTENDANCE_DETAILS:
                return Util.buildResponse(attendanceService.getAttendanceDetails(requestBody.getInt("meetingId")));
            case ADD_UPDATE_ATTENDANCE:
                return Util.buildResponse(attendanceService.addAttendanceContribution(AttendanceUpdateDto.fromJsonObject(requestBody)));
            case DEDUCT_UPDATE_ATTENDANCE:
                return Util.buildResponse(attendanceService.deductAttendanceContribution(AttendanceUpdateDto.fromJsonObject(requestBody)));
            default:
                throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }
    }
}
