package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Attendance;
import com.jacpower.groupsApp.model.AttendanceUpdateDto;
import com.jacpower.groupsApp.model.Group;
import com.jacpower.groupsApp.ruleEngine.engine.Engine;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    private final Engine engine;

    @Autowired
    public AttendanceController(Engine engine) {
        this.engine = engine;
    }

    @PostMapping(path = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addAttendance(@RequestBody @Valid Attendance attendance){
        JsonObject payload= Json.createObjectBuilder(attendance.toJsonObject())
                .add(Constants.REQUEST_TYPE, RequestTypes.ADD_ATTENDANCE.name())
                .build();
        return engine.routeRequest(payload, Modules.ATTENDANCE.name());
    }

    @GetMapping(path = "/get/{meetingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAttendanceDetails(@PathVariable ("meetingId") int meetingId){
        JsonObject payload= Json.createObjectBuilder()
                .add("meetingId", meetingId)
                .add(Constants.REQUEST_TYPE, RequestTypes.GET_ATTENDANCE_DETAILS.name())
                .build();
        return engine.routeRequest(payload, Modules.ATTENDANCE.name());
    }
    @PutMapping(path = "/update/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addUpdateAttendanceDetails(@RequestBody @Valid AttendanceUpdateDto attendance){
        JsonObject payload= Json.createObjectBuilder(attendance.toJsonObject())
                .add(Constants.REQUEST_TYPE, RequestTypes.ADD_UPDATE_ATTENDANCE.name())
                .build();
        return engine.routeRequest(payload, Modules.ATTENDANCE.name());
    }
    @PutMapping(path = "/update/deduct", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deductUpdateAttendanceDetails(@RequestBody @Valid AttendanceUpdateDto attendance){
        JsonObject payload= Json.createObjectBuilder(attendance.toJsonObject())
                .add(Constants.REQUEST_TYPE, RequestTypes.DEDUCT_UPDATE_ATTENDANCE.name())
                .build();
        return engine.routeRequest(payload, Modules.ATTENDANCE.name());
    }

}
