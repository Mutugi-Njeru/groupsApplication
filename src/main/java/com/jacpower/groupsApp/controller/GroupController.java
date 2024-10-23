package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
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

@RestController
@CrossOrigin("*")
@RequestMapping("/group")
public class GroupController {
    private final Engine engine;
    @Autowired
    public GroupController(Engine engine) {
        this.engine = engine;
    }

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registerOrg(@RequestBody @Valid Group group){
        JsonObject payload= Json.createObjectBuilder(group.toJsonObject())
                .add(Constants.REQUEST_TYPE, RequestTypes.CREATE_GROUP.name())
                .build();
        return engine.routeRequest(payload, Modules.GROUP.name());
    }
    @GetMapping(path = "/get/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getGroupDetails(@PathVariable ("userId") int userId){
        JsonObject payload=Json.createObjectBuilder()
                .add("userId", userId)
                .add(Constants.REQUEST_TYPE, RequestTypes.GET_GROUP_DETAILS.name())
                .build();
        return engine.routeRequest(payload, Modules.GROUP.name());
    }
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateGroupDetails(@RequestBody @Valid Group group){
        JsonObject payload=Json.createObjectBuilder(group.toJsonObject())
                .add(Constants.REQUEST_TYPE, RequestTypes.UPDATE_GROUP.name())
                .build();
        return engine.routeRequest(payload, Modules.GROUP.name());
    }




}
