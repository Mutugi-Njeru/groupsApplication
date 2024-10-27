package com.jacpower.groupsApp.ruleEngine.rules.meeting;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Meeting;
import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import com.jacpower.groupsApp.ruleEngine.service.MeetingService;
import com.jacpower.groupsApp.utility.Constants;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class MeetingImplRule implements ServiceRule {
    private final MeetingService meetingService;

    @Autowired
    public MeetingImplRule(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.MEETING.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Json.createReader(new StringReader(request.toString())).readObject();
        String requestType = requestBody.getString(Constants.REQUEST_TYPE, "");

        switch (RequestTypes.valueOf(requestType)){
            case ADD_MEETING:
                return Util.buildResponse(meetingService.addMeeting(Meeting.fromJsonObject(requestBody)));
            case GET_GROUP_MEETINGS:
                return Util.buildResponse(meetingService.getGroupMeetings(requestBody.getInt("userId")));
            case CLOSE_MEETING:
                return Util.buildResponse(meetingService.closeMeeting(requestBody.getInt("meetingId")));
            default:
                throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }
    }
}
