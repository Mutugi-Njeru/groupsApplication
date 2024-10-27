package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Group;
import com.jacpower.groupsApp.model.Meeting;
import com.jacpower.groupsApp.ruleEngine.engine.Engine;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/meeting")
public class MeetingController {
    private final Engine engine;
    @Autowired
    public MeetingController(Engine engine) {
        this.engine = engine;
    }
    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addMeeting(@RequestBody @Valid Meeting meeting){
        JsonObject payload= Json.createObjectBuilder(meeting.toJsonObject())
                .add(Constants.REQUEST_TYPE, RequestTypes.ADD_MEETING.name())
                .build();
        return engine.routeRequest(payload, Modules.MEETING.name());
    }

    @GetMapping(value = "/get/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getGroupMeetings(@PathVariable ("userId") int userId){
        JsonObject payload= Json.createObjectBuilder()
                .add("userId", userId)
                .add(Constants.REQUEST_TYPE, RequestTypes.GET_GROUP_MEETINGS.name())
                .build();
        return engine.routeRequest(payload, Modules.MEETING.name());
    }
    @PutMapping(path = "/close/{meetingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> closeMeeting(@PathVariable ("meetingId") int meetingId){
        JsonObject payload= Json.createObjectBuilder()
                .add("meetingId", meetingId)
                .add(Constants.REQUEST_TYPE, RequestTypes.CLOSE_MEETING.name())
                .build();
        return engine.routeRequest(payload, Modules.MEETING.name());

    }
}
